package com.ebizprise.project.utility;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class PwdResetApp extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1879009976088314639L;
	/**
	 * 
	 */

	private JComboBox<String> jComboBox;
	private Map<String, String> map = new HashMap<String, String>();
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JFileChooser fc;
	private File selectedFile;
	private JLabel lblNewLabel_3;
	private JCheckBox checkBox;
	private JLabel lblNewLabel_4;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PwdResetApp frame = new PwdResetApp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PwdResetApp() {

		fc = new JFileChooser();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 681, 322);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnNewButton = new JButton("選擇參數檔");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int returnValue = fc.showOpenDialog(null);
				// int returnValue = jfc.showSaveDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					selectedFile = fc.getSelectedFile();
					lblNewLabel_4.setText(selectedFile.getAbsolutePath());
					InputStreamReader in;
					try {
						in = new InputStreamReader(new FileInputStream(selectedFile), "UTF-8");
						BufferedReader file = new BufferedReader(in);

						String line;

						if (jComboBox.getItemCount() > 0) {
							jComboBox.removeAllItems();
							map.clear();
						}

						while ((line = file.readLine()) != null) {
							if (!line.contains("#")) {
								String[] spliStr = line.split("=");
								if (spliStr.length >= 2) {
									map.put(spliStr[0], spliStr[1]);
								}
							}
						}
						file.close();

						for (Object key : map.keySet()) {
							map.put(key.toString(), map.get(key));
							jComboBox.addItem(key.toString());
						}
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});
		btnNewButton.setBounds(14, 13, 142, 27);
		contentPane.add(btnNewButton);

		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(166, 123, 483, 25);
		contentPane.add(textField);
		textField.setColumns(10);

		checkBox = new JCheckBox("加密");
		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkBox.isSelected()) {
					passwordField.setEchoChar('*');
					lblNewLabel_3.setVisible(true);
					passwordField_1.setVisible(true);
				} else {
					lblNewLabel_3.setVisible(false);
					passwordField_1.setVisible(false);
					passwordField.setEchoChar((char) 0);
				}
				;
			}
		});
		checkBox.setBounds(324, 87, 99, 27);
		contentPane.add(checkBox);

		JButton btnNewButton_1 = new JButton("儲存");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String forReshow = "";
				if (checkBox.isSelected()) {
					if (Arrays.equals(passwordField_1.getPassword(), passwordField.getPassword())) {
						try {
							String encStr = Jasypt.enctyptStr(new String(passwordField.getPassword()));
							forReshow = encStr;
							InputStreamReader in = new InputStreamReader(new FileInputStream(selectedFile), "UTF-8");
							BufferedReader file = new BufferedReader(in);

							StringBuffer inputBuffer = new StringBuffer();
							String line;

							while ((line = file.readLine()) != null) {
								if (!line.startsWith("#")
										&& line.contains(String.valueOf(jComboBox.getSelectedItem()))) {
									String newLine = new String();
									newLine += String.valueOf(jComboBox.getSelectedItem() + "=" + encStr);
									line = newLine;
									inputBuffer.append(line);
									inputBuffer.append('\n');
								} else {
									inputBuffer.append(line);
									inputBuffer.append('\n');
								}
							}
							file.close();
							String inputStr = inputBuffer.toString();

							OutputStreamWriter fileOut = new OutputStreamWriter(new FileOutputStream(selectedFile),
									"utf-8");
							fileOut.append(inputStr);
							fileOut.close();

							JOptionPane.showMessageDialog(null, "成功");
						} catch (FileNotFoundException e1) {
							JOptionPane.showMessageDialog(null, "失敗程式發生錯誤");
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "失敗程式發生錯誤");
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "內容值不一致");
					}
				} else {
					try {
						forReshow = new String(passwordField.getPassword());
						InputStreamReader in = new InputStreamReader(new FileInputStream(selectedFile), "UTF-8");
						BufferedReader file = new BufferedReader(in);

						StringBuffer inputBuffer = new StringBuffer();
						String line;

						while ((line = file.readLine()) != null) {
							if (!line.startsWith("#") && line.contains(String.valueOf(jComboBox.getSelectedItem()))) {
								String newLine = new String();
								newLine += String.valueOf(jComboBox.getSelectedItem()) + "="
										+ new String(passwordField.getPassword());
								line = newLine;
								inputBuffer.append(line);
								inputBuffer.append('\n');
							} else {
								inputBuffer.append(line);
								inputBuffer.append('\n');
							}
						}
						file.close();
						String inputStr = inputBuffer.toString();

						OutputStreamWriter fileOut = new OutputStreamWriter(new FileOutputStream(selectedFile),
								"utf-8");
						fileOut.append(inputStr);
						fileOut.close();

						JOptionPane.showMessageDialog(null, "成功");
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(null, "失敗程式發生錯誤");
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "失敗程式發生錯誤");
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				// 檔案重載
				// key String.valueOf(jComboBox.getSelectedItem()))
				// value new String(passwordField.getPassword()
				String selected = String.valueOf(jComboBox.getSelectedItem());
				map.put(String.valueOf(jComboBox.getSelectedItem()), forReshow);
				if (jComboBox.getItemCount() > 0) {
					jComboBox.removeAllItems();
				}
				for (Object key : map.keySet()) {
					jComboBox.addItem(key.toString());
				}
				jComboBox.setSelectedItem(selected);
			}
		});

		btnNewButton_1.setBounds(166, 237, 99, 27);
		contentPane.add(btnNewButton_1);

		JLabel lblNewLabel = new JLabel("參數名稱");
		lblNewLabel.setBounds(14, 91, 142, 19);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("舊參數值");
		lblNewLabel_1.setBounds(14, 126, 142, 19);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("新參數值");
		lblNewLabel_2.setBounds(14, 164, 142, 19);
		contentPane.add(lblNewLabel_2);

		lblNewLabel_3 = new JLabel("確認新參數值");
		lblNewLabel_3.setBounds(14, 202, 142, 19);
		contentPane.add(lblNewLabel_3);

		passwordField = new JPasswordField();
		passwordField.setBounds(166, 161, 483, 25);
		contentPane.add(passwordField);

		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(166, 199, 483, 25);
		contentPane.add(passwordField_1);

		jComboBox = new JComboBox<String>();
		jComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				passwordField.setText("");
				passwordField_1.setText("");
				textField.setText(map.get(String.valueOf(jComboBox.getSelectedItem())));
			}
		});
		jComboBox.setBounds(166, 88, 147, 25);
		contentPane.add(jComboBox);

		lblNewLabel_4 = new JLabel("");
		lblNewLabel_4.setBounds(14, 59, 635, 19);
		contentPane.add(lblNewLabel_4);

		lblNewLabel_3.setVisible(false);
		passwordField_1.setVisible(false);
		passwordField.setEchoChar((char) 0);

	}
}
