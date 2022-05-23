package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import javafx.scene.control.Label;

import javafx.scene.control.PasswordField;

public class Controller {

	public Controller() {

	}

	@FXML
	private TextField userId;
	@FXML
	private PasswordField userPwd;
	@FXML
	private Button btn;
	@FXML
	private Label missatgeConn;

	public void loginUsuari(ActionEvent event) {
		try {
			confirmarLogin();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String usuariConnectat;
	
	private void confirmarLogin() throws IOException, SQLException {
		Main m = new Main();
		chatLoguejatController clc = new chatLoguejatController();
		
		Connection conn = null;

		String id = userId.getText().toString();
		String pwd = userPwd.getText().toString();

		conn = DriverManager.getConnection("jdbc:mysql://192.168.1.218:3306/chat", "admin", "Fat/3232");
		Statement st = conn.createStatement();
		String sql = "SELECT * FROM DEL_usuaris WHERE dni='" + id + "' AND clau='" + pwd + "';";
		System.out.println("SELECT * FROM DEL_usuaris WHERE dni='" + id + "' AND clau='" + pwd + "';");
		ResultSet rs = st.executeQuery(sql);
		while (rs.next()) {
			usuariConnectat = rs.getString("nom");
			if (userId.getText().toString().equals(rs.getString("dni"))
					&& userPwd.getText().toString().equals(rs.getString("clau"))) {
				missatgeConn.setText("Conectat!");

				m.cambiarEscena("chatLoguejat.fxml");
				clc.obtencioDades(id, usuariConnectat);
				clc.arrencarClient();
			}
		}
		if (!rs.next()) {
			missatgeConn.setText("Credencials incorrectes!");
		}
		if (userId.getText().isEmpty() && userPwd.getText().isEmpty()) {
			missatgeConn.setText("Insereix les credencials");
		}
	}

}
