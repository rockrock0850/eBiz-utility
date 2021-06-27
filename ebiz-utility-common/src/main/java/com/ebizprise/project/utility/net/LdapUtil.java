package com.ebizprise.project.utility.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.*;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

/**
 * @author gary.tsai 2019/5/16
 */
public class LdapUtil {
    private static Log logger = LogFactory.getLog(LdapUtil.class);

    private String username;
    private String password;
    private String ldapDomain;
    private String ldapPort;
    private String ldapDn;
    private LdapContext ctx;

    public LdapUtil(String username, String password, String ldapDomain, String ldapPort, String ldapDn)
            throws NamingException {
        if (null == ldapDomain || ldapDomain.equals("")) {
            throw new CommunicationException("請輸入 LDAP Domain");
        } else if (null == ldapPort || ldapPort.equals("")) {
            throw new CommunicationException("請輸入 LDAP PORT");
        } else if (null == ldapDn || ldapDn.equals("")) {
            throw new CommunicationException("請輸入 LDAP DN");
        }
        this.username = username;
        this.password = password;
        this.ldapDomain = ldapDomain;
        this.ldapPort = ldapPort;
        this.ldapDn = ldapDn;
    }

    public DirContext getLdapContext() throws NamingException {
        if (null == username || username.equals("")) {
            throw new AuthenticationException("請輸入使用者名稱");
        } else if (null == password || password.equals("")) {
            throw new AuthenticationException("請輸入使用者密碼");
        }

        try {
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");

            // NOTE: replace user@domain.com with a User that is present in your
            // Active Directory/LDAP
            env.put(Context.SECURITY_PRINCIPAL, username);
            // NOTE: replace userpass with passwd of this user.
            env.put(Context.SECURITY_CREDENTIALS, password);
            // NOTE: replace ADorLDAPHost with your Active Directory/LDAP
            // Hostname or IP.
            env.put(Context.PROVIDER_URL, "ldap://" + ldapDomain + ":" + ldapPort + "/");
            // env.put("java.naming.referral", "follow");
            env.put(Context.REFERRAL, "follow");

            logger.info("LDAP 嘗試連線中...");

            ctx = new InitialLdapContext(env, null);
            logger.info("LDAP 連線成功.");
        } catch (AuthenticationException ae) {
            logger.error("使用者帳號或密碼錯誤");
            throw ae;
        } catch (CommunicationException ce) {
            logger.error("LDAP 連線愈時");
            throw ce;
        } catch (NamingException nex) {
            logger.error("LDAP 連線失敗");
            throw nex;
        }
        return ctx;
    }

    public Attributes loginLdap() throws NamingException {
        if (Objects.isNull(ctx)) {
            throw new CommunicationException("請初始 Ldap 連線!!");
        }
        Attributes attrs;
        try {
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // NOTE: The attributes mentioned in array below are the ones that
            // will be retrieved, you can add more.
            String[] attrIDs = {"distinguishedName", "sn", "givenname", "mail", "telephonenumber", "canonicalName",
                    "userAccountControl", "accountExpires"};
            searchControls.setReturningAttributes(attrIDs);
            NamingEnumeration<?> answer = ctx.search(ldapDn,
                    "(&(objectClass=user)(sAMAccountName=" + username.split("@")[0] + "))", searchControls);

            if (answer.hasMoreElements()) {
                SearchResult rslt = (SearchResult) answer.next();
                attrs = rslt.getAttributes();
            } else {
                throw new AuthenticationException("查無使用者");
            }
            if (null != answer) {
                answer.close();
            }
        } catch (AuthenticationException ae) {
            throw ae;
        }
        return attrs;
    }

    public List<Attributes> getLdapAllUsersInfo() throws Exception {
        return getAllInfoByObjectClass("user");
    }

    public List<Attributes> getLdapAllGroupsInfo() throws Exception {
        return getAllInfoByObjectClass("group");
    }

    public List<Attributes> getAllInfoByObjectClass(String objectClass) throws Exception {
        if (Objects.isNull(ctx)) {
            throw new Exception("請初始 Ldap 連線!!");
        }
        List<Attributes> attributesList = new ArrayList<>();
        try {
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            //Set the page size and initialize the cookie that we pass back in subsequent pages
            int pageSize = 10;
            byte[] cookie;

            //Request the paged results control
            Control[] ctls = new Control[]{new PagedResultsControl(pageSize, true)};
            ctx.setRequestControls(ctls);

            //initialize counter to total the results
            int totalResults = 0;

            // Search for objects using the filter
            do {

                NamingEnumeration<?> results = ctx.search(ldapDn,
                        "(&(objectClass=" + objectClass + "))", searchControls);

                // loop through the results in each page
                while (results != null && results.hasMoreElements()) {
                    SearchResult sr = (SearchResult) results.next();
                    attributesList.add(sr.getAttributes());
                    //increment the counter
                    totalResults++;
                }

                // examine the response controls
                cookie = parseControls(ctx.getResponseControls());
                // pass the cookie back to the server for the next page
                ctx.setRequestControls(new Control[]{new
                        PagedResultsControl(pageSize
                        , cookie, Control.CRITICAL)});


            } while ((cookie != null) && (cookie.length != 0));
            logger.info("Total entries: " + totalResults);
        } catch (Exception ex) {
            throw ex;
        } finally {
            closeConnection();
        }
        return attributesList;
    }

    public void closeConnection() {
        if (!Objects.isNull(ctx)) {
            try {
                ctx.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] parseControls(Control[] controls) {

        byte[] cookie = null;

        if (controls != null) {
            for (int i = 0; i < controls.length; i++) {
                if (controls[i] instanceof PagedResultsResponseControl) {
                    PagedResultsResponseControl prrc =
                            (PagedResultsResponseControl) controls[i];
                    cookie = prrc.getCookie();
                }

            }

        }
        return (cookie == null) ? new byte[0] : cookie;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
