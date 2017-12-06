import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

public class Usuario extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	public Cliente cliente;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					(new Thread(new Servidor())).start();
						                
					Usuario frame = new Usuario();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Usuario() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(76, 16, 112, 23);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(271, 16, 112, 23);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setBounds(76, 45, 112, 23);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		JButton btnNewButton = new JButton("Iniciar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//textPane.setText(textField.getText());
				
				try {
					cliente = new Cliente("localhost", 3000);
					Thread client;
	                client = new Thread(cliente);
	                client.start();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
                
				;
				
			}
		});
		btnNewButton.setBounds(312, 129, 89, 23);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Cancelar");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					cliente.setCancelado(true);
				} catch (InterruptedException e1) {
					
					e1.printStackTrace();
				}
			}
		});
		btnNewButton_1.setBounds(312, 227, 89, 23);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Pausar");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cliente.setPausado(true);
			}
		});
		btnNewButton_2.setBounds(312, 160, 89, 23);
		contentPane.add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("Reiniciar");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cliente.setPausado(false);
			}
		});
		btnNewButton_3.setBounds(312, 193, 89, 23);
		contentPane.add(btnNewButton_3);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(186, 143, 37, 23);
		contentPane.add(textPane);
		
		JLabel lblIp = new JLabel("SERVIDOR ");
		lblIp.setBounds(10, 0, 63, 14);
		contentPane.add(lblIp);
		
		JLabel lblNewLabel = new JLabel("CLIENTE");
		lblNewLabel.setBounds(198, 0, 46, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblIpDoCliente = new JLabel("IP do Cliente:");
		lblIpDoCliente.setBounds(10, 20, 74, 14);
		contentPane.add(lblIpDoCliente);
		
		JLabel lblNewLabel_1 = new JLabel("IP do Servidor:");
		lblNewLabel_1.setBounds(198, 20, 74, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblPorta = new JLabel("Porta:");
		lblPorta.setBounds(42, 50, 46, 14);
		contentPane.add(lblPorta);
	}
}
