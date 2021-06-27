package com.ebizprise.project.utility.doc.velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

public final class VelocityUtil {

	private VelocityEngine ve;

	private HttpServletRequest request;

	private String resourcePath;

	private String dir;

	public VelocityUtil() {
	}

	public void initClassPath() {
		Properties p = new Properties();
		p.put(RuntimeConstants.RESOURCE_LOADER, "classpath");
		p.put("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		init(p);
	}

	public void initFilePath() {
		Properties p = new Properties();
		p.put(RuntimeConstants.RESOURCE_LOADER, "file");
		p.put("file.resource.loader.class", FileResourceLoader.class.getName());
		init(p);
	}

	/**
	 *
	 */
	public void initFileSystemPath(String templateFilePath) {
		Properties p = new Properties();
		p.put(RuntimeConstants.RESOURCE_LOADER, "file");
		if (null != templateFilePath && !"".equals(templateFilePath)) {
			p.put(Velocity.FILE_RESOURCE_LOADER_PATH, templateFilePath);
		} else {
			p.put(Velocity.FILE_RESOURCE_LOADER_PATH,
					null == resourcePath || "".equals(resourcePath) ? "tempalte/" : resourcePath);
		}
		init(p);
	}

	/**
	 *
	 */
	public void initWebPath(HttpServletRequest request, String templateFilePath) {
		this.request = request;
		Properties p = new Properties();
		p.put(RuntimeConstants.RESOURCE_LOADER, "webapp");
		p.put("webapp.resource.loader.class", "org.apache.velocity.tools.view.servlet.WebappLoader");
		if (null != templateFilePath && !"".equals(templateFilePath)) {
			p.put("webapp.resource.loader.path", templateFilePath);
		} else {
			p.put("webapp.resource.loader.path",
					null == resourcePath || "".equals(resourcePath) ? "/WEB-INF/template/" : resourcePath);
		}
		init(p);
	}

	public void writeTemplateOutput(String templateName, String outputFile, Map<String, Object> map) throws Exception {
		try {
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8.name()),
						1024 * 1024);
				bw.write(generateContect(templateName, map));
				bw.flush();
			} catch (Exception e) {
				throw e;
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public String generateContect(String templateName, Map<String, Object> map) {
		StringWriter writer = new StringWriter();
		if (null != dir && "" != dir) {
			templateName = dir + File.separator + templateName;
		}

		try {

			Template template = ve.getTemplate(templateName);

			VelocityContext context = new VelocityContext();
			Set<String> keys = map.keySet();
			for (String key : keys) {
				context.put(key, map.get(key));
			}

			template.merge(context, writer);

		} catch (Exception e) {
			throw e;
		}
		return writer.toString();
	}

	private void init(Properties p) {
		p.put(Velocity.INPUT_ENCODING, StandardCharsets.UTF_8.name());
		p.put(Velocity.OUTPUT_ENCODING, StandardCharsets.UTF_8.name());
		if (null == ve) {
			ve = new VelocityEngine(p);
			if (!Objects.isNull(request)) {
				ve.setApplicationAttribute("javax.servlet.ServletContext", request.getSession().getServletContext());
			}
			ve.init();
		}
	}

	public void close() {
		if (null != ve) {
			ve = null;
		}
	}

	public static void main(String[] args) {
		VelocityUtil createHtml = new VelocityUtil();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "Gary Tsai");

		try {
			createHtml.writeTemplateOutput("template/hello.vm", "/home/zipe/tmp/test.html", map);

			// VelocityUtil createHtml2 = new VelocityUtil();
			//
			// createHtml2.writeTemplateOutput("template/hello.vm",
			// "/home/zipe/tmp/test2.html", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
}