package semi;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RootController implements Initializable {
	@FXML Button btnLogin, btnFindId, btnFindPass, btnAdd;
	
	private Stage primaryStage;
	void SetPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	
	static String userid;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		btnLogin.setOnAction(event->btnLogin(event));
		btnFindId.setOnAction(event->btnFindId(event));
		btnFindPass.setOnAction(event->btnFindPass(event));
		btnAdd.setOnAction(event->btnAdd(event));
	}
	
	public void btnLogin(ActionEvent event) {	// �α��� ��ư ������ �� 
		try {
			Stage stage = new Stage(StageStyle.UTILITY);	// �α��� â
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(primaryStage);
			stage.setTitle("�α���");
			
			conn = DBConn.getConnection();
			
			Parent login = (Parent)FXMLLoader.load(getClass().getResource("login.fxml"));
			TextField mem_userid = (TextField) login.lookup("#logId");
			PasswordField mem_pass = (PasswordField) login.lookup("#logPass");
			Button logBtnLogin = (Button) login.lookup("#logBtnLogin");
			logBtnLogin.setOnAction(e->{
				try {
					String sql = "SELECT mem_idx FROM sc_member WHERE mem_userid=? AND mem_pass=PASSWORD(?)";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, mem_userid.getText());
					pstmt.setString(2, mem_pass.getText());
					rs = pstmt.executeQuery();
					if(rs.next()) {
						userid = mem_userid.getText();
						stage.close();
						Parent scMain = FXMLLoader.load(getClass().getResource("studycafeMain.fxml"));
						StackPane root = (StackPane) btnLogin.getScene().getRoot();
						root.getChildren().add(scMain);
					}else {
						Stage dialog = new Stage(StageStyle.UTILITY);	// Ȯ�� â
						dialog.initModality(Modality.WINDOW_MODAL);
						dialog.initOwner(primaryStage);
						dialog.setTitle("!������ �߻��Ͽ����ϴ�.");
						
						Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
						ImageView imageView = (ImageView) check.lookup("#imageView");
						imageView.setImage(new Image(getClass().getResource("error.png").toString()));
						Button btnOk = (Button) check.lookup("#btnOk");
						btnOk.setOnAction(e2->{
							dialog.close();
						});
						Label txtTitle = (Label) check.lookup("#txtTitle");
						txtTitle.setText("���̵� �Ǵ� ��й�ȣ�� Ȯ�����ּ���.");
						
						Scene scene = new Scene(check);
						
						dialog.setScene(scene);
						dialog.setResizable(false);
						dialog.show();
					}
				}catch(Exception exception) {
					exception.printStackTrace();
				}
			});
			
			Scene scene = new Scene(login);
			
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void btnFindId(ActionEvent event) {
		try {
			Stage stage = new Stage(StageStyle.UTILITY);	// ���̵� ã�� â
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(primaryStage);
			stage.setTitle("���̵� ã��");
			
			conn = DBConn.getConnection();
			
			Parent findId = FXMLLoader.load(getClass().getResource("findId.fxml"));
			TextField mem_name = (TextField) findId.lookup("#findName");
			TextField mem_ph = (TextField) findId.lookup("#findPh");
			Button findBtnId = (Button) findId.lookup("#findBtnId");
			findBtnId.setOnAction(e->{
				try {
					String sql = "SELECT mem_userid FROM sc_member WHERE mem_name=? AND mem_ph=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, mem_name.getText());
					pstmt.setString(2, mem_ph.getText());
					rs = pstmt.executeQuery();
					if(rs.next()) {
						Stage dialog = new Stage(StageStyle.UTILITY);	// Ȯ�� â
						dialog.initModality(Modality.WINDOW_MODAL);
						dialog.initOwner(primaryStage);
						dialog.setTitle("���̵� ã��.");
						
						Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
						ImageView imageView = (ImageView) check.lookup("#imageView");
						imageView.setImage(new Image(getClass().getResource("next.png").toString()));
						Button btnOk = (Button) check.lookup("#btnOk");
						btnOk.setOnAction(e2->{
							dialog.close();
							stage.close();
						});
						Label txtTitle = (Label) check.lookup("#txtTitle");
						txtTitle.setText("'" + mem_name.getText() + "'���� ���̵�� " + rs.getString("mem_userid") + " �Դϴ�.");
						
						Scene scene = new Scene(check);
						
						dialog.setScene(scene);
						dialog.setResizable(false);
						dialog.show();
					}else {
						Stage dialog = new Stage(StageStyle.UTILITY);	// Ȯ�� â
						dialog.initModality(Modality.WINDOW_MODAL);
						dialog.initOwner(primaryStage);
						dialog.setTitle("!������ �߻��Ͽ����ϴ�.");
						
						Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
						ImageView imageView = (ImageView) check.lookup("#imageView");
						imageView.setImage(new Image(getClass().getResource("error.png").toString()));
						Button btnOk = (Button) check.lookup("#btnOk");
						btnOk.setOnAction(e2->{
							dialog.close();
							stage.close();
						});
						Label txtTitle = (Label) check.lookup("#txtTitle");
						txtTitle.setText("��ġ�ϴ� ���̵� �����ϴ�.");
						
						Scene scene = new Scene(check);
						
						dialog.setScene(scene);
						dialog.setResizable(false);
						dialog.show();
					}
				}catch(Exception exception) {
					exception.printStackTrace();
				}
			});
			
			Scene scene = new Scene(findId);
			
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}catch(Exception e2) {
			e2.printStackTrace();
		}
	}
	
	public void btnFindPass(ActionEvent event) {
		try {
			Stage stage = new Stage(StageStyle.UTILITY);	// ��й�ȣ ã�� â
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(primaryStage);
			stage.setTitle("��й�ȣ ã��");
			
			conn = DBConn.getConnection();
			
			Parent findPass = FXMLLoader.load(getClass().getResource("findPass.fxml"));
			TextField mem_userid = (TextField) findPass.lookup("#findId");
			TextField mem_ph = (TextField) findPass.lookup("#findPh");
			Button findBtnPass = (Button) findPass.lookup("#findBtnPass");
			findBtnPass.setOnAction(e->{
				try {
					String sql = "SELECT mem_idx FROM sc_member WHERE mem_userid=? AND mem_ph=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, mem_userid.getText());
					pstmt.setString(2, mem_ph.getText());
					rs = pstmt.executeQuery();
					if(rs.next()) {
						Stage dialog = new Stage(StageStyle.UTILITY);	// ��й�ȣ ���� â
						dialog.initModality(Modality.WINDOW_MODAL);
						dialog.initOwner(primaryStage);
						dialog.setTitle("��й�ȣ ����");
						
						Parent changePass = FXMLLoader.load(getClass().getResource("changePass.fxml"));
						TextField mem_pass = (TextField) changePass.lookup("#changePass");
						Button changeBtnPass = (Button) changePass.lookup("#changeBtnPass");
						changeBtnPass.setOnAction(e2->{
							System.out.println(mem_pass.getText());
							try {
								String sql2 = "UPDATE sc_member SET mem_pass=PASSWORD(?) WHERE mem_userid=? AND mem_ph=?";
								pstmt = conn.prepareStatement(sql2);
								pstmt.setString(1, mem_pass.getText());
								pstmt.setString(2, mem_userid.getText());
								pstmt.setString(3, mem_ph.getText());
								int result = pstmt.executeUpdate();
								System.out.println(result);
								if(result >= 1) {
									Stage dialog2 = new Stage(StageStyle.UTILITY);	// Ȯ�� â
									dialog2.initModality(Modality.WINDOW_MODAL);
									dialog2.initOwner(primaryStage);
									dialog2.setTitle("��й�ȣ ����.");
									
									Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
									ImageView imageView = (ImageView) check.lookup("#imageView");
									imageView.setImage(new Image(getClass().getResource("smile.png").toString()));
									Button btnOk = (Button) check.lookup("#btnOk");
									btnOk.setOnAction(e3->{
										dialog2.close();
										dialog.close();
										stage.close();
									});
									Label txtTitle = (Label) check.lookup("#txtTitle");
									txtTitle.setText("��й�ȣ�� ����Ǿ����ϴ�.");
									
									Scene scene = new Scene(check);
									
									dialog2.setScene(scene);
									dialog2.setResizable(false);
									dialog2.show();
								}else {
									Stage dialog2 = new Stage(StageStyle.UTILITY);	// Ȯ�� â
									dialog2.initModality(Modality.WINDOW_MODAL);
									dialog2.initOwner(primaryStage);
									dialog2.setTitle("!������ �߻��Ͽ����ϴ�.");
									
									Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
									ImageView imageView = (ImageView) check.lookup("#imageView");
									imageView.setImage(new Image(getClass().getResource("error.png").toString()));
									Button btnOk = (Button) check.lookup("#btnOk");
									btnOk.setOnAction(e3->{
										dialog2.close();
									});
									Label txtTitle = (Label) check.lookup("#txtTitle");
									txtTitle.setText("���̵� �Ǵ� ��ȭ��ȣ�� Ȯ�����ּ���.");
									
									Scene scene = new Scene(check);
									
									dialog2.setScene(scene);
									dialog2.setResizable(false);
									dialog2.show();
								}
								
							} catch (SQLException | IOException e1) {
								e1.printStackTrace();
							}
						});
						
						Scene scene = new Scene(changePass);
						
						dialog.setScene(scene);
						dialog.setResizable(false);
						dialog.show();
					}else {
						Stage dialog = new Stage(StageStyle.UTILITY);	// Ȯ�� â
						dialog.initModality(Modality.WINDOW_MODAL);
						dialog.initOwner(primaryStage);
						dialog.setTitle("!������ �߻��Ͽ����ϴ�.");
						
						Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
						ImageView imageView = (ImageView) check.lookup("#imageView");
						imageView.setImage(new Image(getClass().getResource("error.png").toString()));
						Button btnOk = (Button) check.lookup("#btnOk");
						btnOk.setOnAction(e2->{
							dialog.close();
						});
						Label txtTitle = (Label) check.lookup("#txtTitle");
						txtTitle.setText("���̵� �Ǵ� ��ȭ��ȣ�� Ȯ���ϼ���.");
						
						Scene scene = new Scene(check);
						
						dialog.setScene(scene);
						dialog.setResizable(false);
						dialog.show();
					}
				}catch(Exception exception) {
					exception.printStackTrace();
				}
			});
			
			Scene scene = new Scene(findPass);
			
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}catch(Exception e2) {
			e2.printStackTrace();
		}
	}
	
	public void btnAdd(ActionEvent event) {
		try {
			Stage stage = new Stage(StageStyle.UTILITY);	// ȸ������ â
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(primaryStage);
			stage.setTitle("ȸ������");
			
			conn = DBConn.getConnection();
			
			Parent addMember = FXMLLoader.load(getClass().getResource("addMember.fxml"));
			TextField mem_userid = (TextField) addMember.lookup("#addId");
			Button addBtnIdDup = (Button) addMember.lookup("#addBtnIdDup");
			Label txtIdDup = (Label) addMember.lookup("#txtIdDup");
			addBtnIdDup.setOnAction(e->{
				try {
					String sql = "SELECT mem_idx FROM sc_member WHERE mem_userid=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, mem_userid.getText());
					rs = pstmt.executeQuery();
					if(rs.next()) {
						txtIdDup.setText("�̹� ������� ���̵��Դϴ�.");
					}else txtIdDup.setText("��� ������ ���̵��Դϴ�.");
				}catch(Exception exception) {
					exception.printStackTrace();
				}
			});
			TextField mem_pass = (TextField) addMember.lookup("#addPass");
			TextField mem_name = (TextField) addMember.lookup("#addName");
			TextField mem_ph = (TextField) addMember.lookup("#addPh");
			Button addBtnPhDup = (Button) addMember.lookup("#addBtnPhDup");
			Label txtPhDup = (Label) addMember.lookup("#txtPhDup");
			addBtnPhDup.setOnAction(e->{
				try {
					String sql = "SELECT mem_idx FROM sc_member WHERE mem_ph=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, mem_ph.getText());
					rs = pstmt.executeQuery();
					if(rs.next()) {
						txtPhDup.setText("�̹� ������� ��ȭ��ȣ�Դϴ�.");
					}else txtPhDup.setText("��� ������ ��ȭ��ȣ�Դϴ�.");
				}catch(Exception exception) {
					exception.printStackTrace();
				}
			});
			TextField mem_email = (TextField) addMember.lookup("#addEmail");
			Button addBtnAdd = (Button) addMember.lookup("#addBtnAdd");
			addBtnAdd.setOnAction(e->{
				if(mem_userid.getText() !=null && mem_pass.getText() !=null && 
						mem_name.getText() !=null && mem_ph.getText() !=null) {
					try {
						String sql = "INSERT INTO sc_member (mem_userid, mem_pass, mem_name, mem_ph, mem_email) "
								+ "VALUE (?, PASSWORD(?), ?, ?, ?);";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, mem_userid.getText());
						pstmt.setString(2, mem_pass.getText());
						pstmt.setString(3, mem_name.getText());
						pstmt.setString(4, mem_ph.getText());
						pstmt.setString(5, mem_email.getText());
						int result = pstmt.executeUpdate();
						if(result >= 1) {
							Stage dialog = new Stage(StageStyle.UTILITY);	// Ȯ�� â
							dialog.initModality(Modality.WINDOW_MODAL);
							dialog.initOwner(primaryStage);
							dialog.setTitle("ȯ���մϴ�.");
							
							Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
							ImageView imageView = (ImageView) check.lookup("#imageView");
							imageView.setImage(new Image(getClass().getResource("smile.png").toString()));
							Button btnOk = (Button) check.lookup("#btnOk");
							btnOk.setOnAction(e2->{
								dialog.close();
								stage.close();
							});
							Label txtTitle = (Label) check.lookup("#txtTitle");
							txtTitle.setText("ȸ�������� �Ϸ�Ǿ����ϴ�.");
							
							Scene scene = new Scene(check);
							
							dialog.setScene(scene);
							dialog.setResizable(false);
							dialog.show();
						}else {
							Stage dialog = new Stage(StageStyle.UTILITY);	// Ȯ�� â
							dialog.initModality(Modality.WINDOW_MODAL);
							dialog.initOwner(primaryStage);
							dialog.setTitle("!������ �߻��Ͽ����ϴ�.");
							
							Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
							ImageView imageView = (ImageView) check.lookup("#imageView");
							imageView.setImage(new Image(getClass().getResource("error.png").toString()));
							Button btnOk = (Button) check.lookup("#btnOk");
							btnOk.setOnAction(e2->{
								dialog.close();
							});
							Label txtTitle = (Label) check.lookup("#txtTitle");
							txtTitle.setText("ȸ�������� �����Ͽ����ϴ�.");
							
							Scene scene = new Scene(check);
							
							dialog.setScene(scene);
							dialog.setResizable(false);
							dialog.show();
						}
					}catch(Exception e2) {
						e2.printStackTrace();
					}
//				}else if(mem_userid.getText().equals(null) || mem_pass.getText().equals(null) ||
//						mem_name.getText().equals(null) || mem_ph.getText().equals(null)) {
//					System.out.println("�ʼ� ������ �Է����ּ���.");
				}
			});
			
			Scene scene = new Scene(addMember);
			
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
