/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 *
 * Login screen for the IM-client part
 */

/**
 *
 * @author miniwolf
 */

public class LoginScreen extends javax.swing.JFrame {

    /** Creates new form LoginScreen */
    public LoginScreen() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */

	private void initComponents() {

	    jUsername = new javax.swing.JTextField();
	    jPassword = new javax.swing.JPasswordField();
	    jPasswordRepeat = new javax.swing.JPasswordField();
	    jCreateUser = new javax.swing.JLabel();
	    jLoginBtn = new javax.swing.JButton();
	    jMenuBar1 = new javax.swing.JMenuBar();
	    menuFile = new javax.swing.JMenu();
	    menuHelp = new javax.swing.JMenu();

	    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	    setName("Fagi"); // NOI18N

	    jUsername.setText("Username...");

	    jPassword.setText("Password...");

	    jPasswordRepeat.setText("Repeat Password...");
	    jPasswordRepeat.setVisible(false);
	    
	    jCreateUser.setText("Create Username");
	    jCreateUser.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
			jCreateUserMouseClicked(evt);
		    }
		});
	    
	    jLoginBtn.setText("Login");
	    jLoginBtn.addActionListener(new java.awt.event.ActionListener() {
		    public void actionPerformed(java.awt.event.ActionEvent evt) {
			jLoginBtnActionPerformed(evt);
		    }
		});

	    menuFile.setText("File");
	    jMenuBar1.add(menuFile);

	    menuHelp.setText("Help");
	    jMenuBar1.add(menuHelp);

	    setJMenuBar(jMenuBar1);

	    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	    getContentPane().setLayout(layout);
	    layout.setHorizontalGroup(
				      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				      .addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							  .addGroup(layout.createSequentialGroup()
								    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
									      .addComponent(jPassword, javax.swing.GroupLayout.Alignment.LEADING)
									      .addComponent(jUsername, 
											    javax.swing.GroupLayout.Alignment.LEADING, 
											    javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
								    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								    .addComponent(jLoginBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
							  .addComponent(jCreateUser))
						.addContainerGap())
				      );
	    layout.setVerticalGroup( layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING )
				    .addGroup( layout.createSequentialGroup()
					       .addGap(34, 34, 34)
					       .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
							 .addComponent(jUsername,
								       javax.swing.GroupLayout.PREFERRED_SIZE, 
								       javax.swing.GroupLayout.DEFAULT_SIZE, 
								       javax.swing.GroupLayout.PREFERRED_SIZE)
							 .addComponent(jLoginBtn))
					       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					       .addComponent(jPassword, 
							     javax.swing.GroupLayout.PREFERRED_SIZE, 
							     javax.swing.GroupLayout.DEFAULT_SIZE, 
							     javax.swing.GroupLayout.PREFERRED_SIZE)
					       .addGap(51, 51, 51)
					       .addComponent(jCreateUser)
					       .addContainerGap(26, Short.MAX_VALUE) 
					       )
				     );
	    
	    pack();
	}
    
    private void jPasswordActionPerformed(java.awt.event.ActionEvent evt) {                                            
	// TODO add your handling code here:
    }                                           

    private void jCreateUserMouseClicked(java.awt.event.MouseEvent evt) {                                     
	System.out.println("CreateUser been pressed");
	jPasswordRepeat.setVisible(true);
    }                                    

    private void jLoginBtnActionPerformed(java.awt.event.ActionEvent evt) {                                         
	System.out.println("LoginBtn been pressed");
	//  User.Login();
    }                                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

		public void run() {
		    new LoginScreen().setVisible(true);
		}
	    });
    }
    // Variables declaration
    private javax.swing.JButton jLoginBtn;
    private javax.swing.JLabel jCreateUser;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JTextField jUsername;
    private javax.swing.JPasswordField jPassword;
    private javax.swing.JPasswordField jPasswordRepeat;
    // End of variables declaration                   
}
