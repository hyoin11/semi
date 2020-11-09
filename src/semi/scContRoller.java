package semi;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class scContRoller implements Initializable {
	@FXML Button btnMain, btnReserve, btnMember;
	@FXML Pane scMain, main, reserve, member;
	
	@FXML Label memberInfor;
	@FXML ListView<Reservation> txtReserveInfor;
	@FXML Button btnDrop;
	
	@FXML DatePicker rDate;
	@FXML Button rBtnP1, rBtnP2, rBtnP3, rBtnP4, rBtnP5;
	@FXML Button rBtn1, rBtn2, rBtn3, rBtn4, rBtn5, rBtn6, rBtn7, rBtn8, rBtn9, rBtn10;
	@FXML Button rBtn11, rBtn12, rBtn13, rBtn14, rBtn15, rBtn16, rBtn17, rBtn18, rBtn19, rBtn20;
	@FXML Button rBtnR1, rBtnR2, rBtnR3, rBtnR4;
	@FXML Label rReserveInfor;
	@FXML Button rBtnReserve;
	
	@FXML Label mId, mPh, mEmail;
	@FXML Button mBtnPass, mBtnPh, mBtnEmail, mBtnDrop, mBtnOut;
	
	private Stage primaryStage;
	void SetPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	
	RootController rc;
	
	ObservableList<Reservation> list = FXCollections.observableArrayList();
	
	String mem_idx, mem_userid, mem_name, mem_ph, mem_email;
	String pro_idx, pro_date, pro_seat, pro_price;
	String re_date;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ImageView left = new ImageView(new Image(getClass().getResource("left.png").toString()));
		btnMain.setGraphic(left);
		ImageView center = new ImageView(new Image(getClass().getResource("center.png").toString()));
		btnReserve.setGraphic(center);
		ImageView right = new ImageView(new Image(getClass().getResource("right.png").toString()));
		btnMember.setGraphic(right);
		memberInfor.setText("'" + rc.userid + "'님의 예약정보");
		
		try {
			conn = DBConn.getConnection();
			
			String sql = "SELECT mem_idx, mem_userid, mem_name, mem_ph, mem_email FROM sc_member WHERE mem_userid=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, rc.userid);
			rs = pstmt.executeQuery();
			rs.next();
			mem_idx = rs.getString("mem_idx");
			mem_userid = rs.getString("mem_userid");
			mem_name = rs.getString("mem_name");
			mem_ph = rs.getString("mem_ph");
			mem_email = rs.getString("mem_email");
			
			sql = "SELECT re_date, re_product FROM sc_reserve WHERE re_member=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mem_idx);
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			if(rs.next()) {
				for(int i=1; i<=rsmd.getColumnCount(); i++) {
					pro_idx = rsmd.getColumnTypeName(i).valueOf(i);
					sql = "SELECT pro_date, pro_seat, pro_price FROM sc_product WHERE pro_idx=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, pro_idx);
					rs = pstmt.executeQuery();
					rs.next();
					pro_date = rs.getString("pro_date");
					pro_seat = rs.getString("pro_seat");
					pro_price = rs.getString("pro_price");
					list.add(new Reservation(pro_date, pro_seat, pro_price));
				}
			}
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		txtReserveInfor.setItems(list);
	}
	
	public void handleBtnMain(ActionEvent e) {	// 예약관리 페이지
		main.toFront();

		btnDrop.setOnAction(event->{	// 예약 취소
			try {
				Stage stage = new Stage(StageStyle.UTILITY);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.initOwner(primaryStage);
				stage.setTitle("예약 취소");
				
				Parent dropReserve = FXMLLoader.load(getClass().getResource("dropReserve.fxml"));
				ListView reserveList = (ListView) dropReserve.lookup("#reserveList");
				reserveList.setItems(list);
				Button btnDropReserve = (Button) dropReserve.lookup("#btnDropReserve");
				btnDropReserve.setOnAction(e3->{
					if(list.isEmpty()) {
						try {
							Stage dialog = new Stage(StageStyle.UTILITY);	// 확인 창
							dialog.initModality(Modality.WINDOW_MODAL);
							dialog.initOwner(primaryStage);
							dialog.setTitle("!취소할 예약이 없습니다.");
							
							Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
							ImageView imageView = (ImageView) check.lookup("#imageView");
							imageView.setImage(new Image(getClass().getResource("error.png").toString()));
							Button btnOk = (Button) check.lookup("#btnOk");
							btnOk.setOnAction(e4->{
								dialog.close();
								stage.close();
								main.toFront();
							});
							Label txtTitle = (Label) check.lookup("#txtTitle");
							txtTitle.setText("!취소할 예약이 없습니다.");
							
							Scene scene = new Scene(check);
							
							dialog.setScene(scene);
							dialog.setResizable(false);
							dialog.show();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}else {
						try {
							conn = DBConn.getConnection();
							int i = reserveList.getSelectionModel().getSelectedIndex();
							String reservation = (String) reserveList.getSelectionModel().getSelectedItem().toString();
							String[] arr = reservation.split("/");
							String sql = "SELECT re_product FROM sc_reserve WHERE re_member=?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, mem_idx);
							rs = pstmt.executeQuery();
							rs.next();
							pro_idx = rs.getString("re_product");
							
							sql = "DELETE FROM sc_reserve WHERE re_product=? AND re_member=?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, pro_idx);
							pstmt.setString(2, mem_idx);
							int result = pstmt.executeUpdate();
							if(result >=  1) {
								sql = "DELETE FROM sc_product WHERE pro_idx=? AND pro_date=? AND pro_seat=?";
								pstmt = conn.prepareStatement(sql);
								pstmt.setString(1, pro_idx);
								pstmt.setString(2, arr[0]);
								pstmt.setString(3, arr[1]);
								result = pstmt.executeUpdate();
								if(result >= 1) {
									Stage dialog = new Stage(StageStyle.UTILITY);	// 확인 창
									dialog.initModality(Modality.WINDOW_MODAL);
									dialog.initOwner(primaryStage);
									dialog.setTitle("취소완료.");
									
									Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
									ImageView imageView = (ImageView) check.lookup("#imageView");
									imageView.setImage(new Image(getClass().getResource("error.png").toString()));
									Button btnOk = (Button) check.lookup("#btnOk");
									btnOk.setOnAction(e4->{
										list.remove(i);
										txtReserveInfor.setItems(list);
										dialog.close();
										stage.close();
										main.toFront();
									});
									Label txtTitle = (Label) check.lookup("#txtTitle");
									txtTitle.setText("예약이 취소되었습니다.");
									
									Scene scene = new Scene(check);
									
									dialog.setScene(scene);
									dialog.setResizable(false);
									dialog.show();
								}
							}
						} catch (IOException | ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
						}
					}
				});
				
				Scene scene = new Scene(dropReserve);
				
				stage.setScene(scene);
				stage.setResizable(false);
				stage.show();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		
	}
	
	public void handleBtnReserve(ActionEvent e) {	// 예약 페이지
		reserve.toFront();
		
		rReserveInfor.setText("");
		rDate.setOnAction(e2->{
			System.out.println();
			LocalDate localDate = rDate.getValue();
			String date = localDate.toString();
			try {
				conn = DBConn.getConnection();
				String sql = "SELECT pro_idx, pro_seat FROM sc_product WHERE pro_date=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, date);
				rs = pstmt.executeQuery();
				rs.next();
				ResultSetMetaData rsmd = rs.getMetaData();
				if(rs.next()) {
					for(int i=1; i<=rsmd.getColumnCount(); i++) {
						pro_idx = rsmd.getColumnTypeName(i).valueOf(i);
						sql = "SELECT pro_seat FROM sc_product WHERE pro_idx=?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, pro_idx);
						rs = pstmt.executeQuery();
						rs.next();
						String seat = rs.getString("pro_seat");
						if(seat.contentEquals("premium Seats1")) rBtnP1.setDisable(true);
						else if (seat.contentEquals("premium Seats2")) rBtnP2.setDisable(true);
						else if (seat.contentEquals("premium Seats3")) rBtnP3.setDisable(true);
						else if (seat.contentEquals("premium Seats4")) rBtnP4.setDisable(true);
						else if (seat.contentEquals("premium Seats5")) rBtnP5.setDisable(true);
						else if (seat.contentEquals("Seat1")) rBtn1.setDisable(true);
						else if (seat.contentEquals("Seat2")) rBtn2.setDisable(true);
						else if (seat.contentEquals("Seat3")) rBtn3.setDisable(true);
						else if (seat.contentEquals("Seat4")) rBtn4.setDisable(true);
						else if (seat.contentEquals("Seat5")) rBtn5.setDisable(true);
						else if (seat.contentEquals("Seat6")) rBtn6.setDisable(true);
						else if (seat.contentEquals("Seat7")) rBtn7.setDisable(true);
						else if (seat.contentEquals("Seat8")) rBtn8.setDisable(true);
						else if (seat.contentEquals("Seat9")) rBtn9.setDisable(true);
						else if (seat.contentEquals("Seat10")) rBtn10.setDisable(true);
						else if (seat.contentEquals("Seat11")) rBtn11.setDisable(true);
						else if (seat.contentEquals("Seat12")) rBtn12.setDisable(true);
						else if (seat.contentEquals("Seat13")) rBtn13.setDisable(true);
						else if (seat.contentEquals("Seat14")) rBtn14.setDisable(true);
						else if (seat.contentEquals("Seat15")) rBtn15.setDisable(true);
						else if (seat.contentEquals("Seat16")) rBtn16.setDisable(true);
						else if (seat.contentEquals("Seat17")) rBtn17.setDisable(true);
						else if (seat.contentEquals("Seat18")) rBtn18.setDisable(true);
						else if (seat.contentEquals("Seat19")) rBtn19.setDisable(true);
						else if (seat.contentEquals("Seat20")) rBtn20.setDisable(true);
						else if (seat.contentEquals("Room1")) rBtnR1.setDisable(true);
						else if (seat.contentEquals("Room2")) rBtnR2.setDisable(true);
						else if (seat.contentEquals("Room3")) rBtnR3.setDisable(true);
						else if (seat.contentEquals("Room4")) rBtnR4.setDisable(true);
					}
				}
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
		});

		rBtnReserve.setOnAction(e2->handleRBtnReserve(e));
	}
	
	public void handleBtnReservation(ActionEvent event) {	// 원하는 자리 선택
		if(event.getSource() == rBtnP1 || event.getSource() == rBtnP2 || event.getSource() == rBtnP3 
				|| event.getSource() == rBtnP4 || event.getSource() == rBtnP5) {
			String date = rDate.getValue().toString();
			String price = "15,000";
			if(event.getSource() == rBtnP1) {
				String seat = "premium Seats1";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtnP2) {
				String seat = "premium Seats2";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtnP3) {
				String seat = "premium Seats3";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtnP4) {
				String seat = "premium Seats4";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtnP5) {
				String seat = "premium Seats5";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}
		}else if(event.getSource() == rBtn1 || event.getSource() == rBtn2 || event.getSource() == rBtn3 ||
				event.getSource() == rBtn4 || event.getSource() == rBtn5 || event.getSource() == rBtn6 ||
				event.getSource() == rBtn7 || event.getSource() == rBtn8 || event.getSource() == rBtn9 ||
				event.getSource() == rBtn10 || event.getSource() == rBtn11 || event.getSource() == rBtn12 ||
				event.getSource() == rBtn13 || event.getSource() == rBtn14 || event.getSource() == rBtn15 ||
				event.getSource() == rBtn16 || event.getSource() == rBtn17 || event.getSource() == rBtn18 ||
				event.getSource() == rBtn19 || event.getSource() == rBtn20) {
			String date = rDate.getValue().toString();
			String price = "10,000";
			if(event.getSource() == rBtn1) {
				String seat = "Seat1";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn2) {
				String seat = "Seat2";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn3) {
				String seat = "Seat3";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn4) {
				String seat = "Seat4";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn5) {
				String seat = "Seat5";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn6) {
				String seat = "Seat6";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn7) {
				String seat = "Seat7";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn8) {
				String seat = "Seat8";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn9) {
				String seat = "Seat9";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn10) {
				String seat = "Seat10";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn11) {
				String seat = "Seat11";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn12) {
				String seat = "Seat12";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn13) {
				String seat = "Seat13";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn14) {
				String seat = "Seat14";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn15) {
				String seat = "Seat15";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn16) {
				String seat = "Seat16";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn17) {
				String seat = "Seat17";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn18) {
				String seat = "Seat18";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn19) {
				String seat = "Seat19";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtn20) {
				String seat = "Seat20";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}
		}else if(event.getSource() == rBtnR1 || event.getSource() == rBtnR3 || event.getSource() == rBtnR4) {
			String date = rDate.getValue().toString();
			String price = "50,000";
			if(event.getSource() == rBtnR1) {
				String seat = "Room1";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtnR3) {
				String seat = "Room3";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}else if(event.getSource() == rBtnR4) {
				String seat = "Room4";
				rReserveInfor.setText(date + "\n" + seat + "\n" + price);
			}
		}else if(event.getSource() == rBtnR2) {
			String date = rDate.getValue().toString();
			String seat = "Room2";
			String price = "80,000";
			rReserveInfor.setText(date + "\n" + seat + "\n" + price);
		}
	}
	
	public void handleRBtnReserve(ActionEvent event) {	// 예약 페이지 예약버튼 누르면
		String[] arr = rReserveInfor.getText().split("\n");
		
		try {
			conn = DBConn.getConnection();
			
			String sql = "INSERT INTO sc_product (pro_date, pro_seat, pro_price) VALUE (?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, arr[0]);
			pstmt.setString(2, arr[1]);
			pstmt.setString(3, arr[2]);
			int result = pstmt.executeUpdate();
			if(result >= 1) {
				sql = "SELECT pro_idx FROM sc_product WHERE pro_date=? AND pro_seat=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, arr[0]);
				pstmt.setString(2, arr[1]);
				rs = pstmt.executeQuery();
				rs.next();
				pro_idx = rs.getString("pro_idx");
				
				sql = "INSERT INTO sc_reserve (re_product, re_member) VALUE (?, ?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, pro_idx);
				pstmt.setString(2, mem_idx);
				result = pstmt.executeUpdate();
				if(result >= 1) {
					Stage stage = new Stage(StageStyle.UTILITY);	// 예약확인 창
					stage.initModality(Modality.WINDOW_MODAL);
					stage.initOwner(primaryStage);
					stage.setTitle("예약확인");
					
					Parent reserveInfor = FXMLLoader.load(getClass().getResource("reserveInfor.fxml"));
					Label txtReserveInfor = (Label) reserveInfor.lookup("#txtReserveInfor");
					txtReserveInfor.setText(arr[0] + "/" + arr[1] + "/" + arr[2]);
					Label txtMemberInfor = (Label) reserveInfor.lookup("#txtMemberInfor");
					txtMemberInfor.setText(mem_name + "/" + mem_ph);
					Button btnInfor = (Button) reserveInfor.lookup("#btnInfor");
					btnInfor.setOnAction(e->{
						list.add(new Reservation(arr[0], arr[1], arr[2]));
						rReserveInfor.setText("");
						stage.close();
						main.toFront();
					});
					
					Scene scene = new Scene(reserveInfor);
					
					stage.setScene(scene);
					stage.setResizable(false);
					stage.show();
				}
			}
		} catch (ClassNotFoundException | SQLException | IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void handleBtnMember(ActionEvent event) throws ClassNotFoundException, SQLException {	// 회원정보 페이지
		member.toFront();

		mId.setText(mem_userid);
		mPh.setText(mem_ph);
		mEmail.setText(mem_email);
		
		rc = new RootController();
		
		mBtnPass.setOnAction(e->handleMBtnPass(e));
		mBtnPh.setOnAction(e->handleMBtnPh(e));
		mBtnEmail.setOnAction(e->handleMBtnEmail(e));
		mBtnDrop.setOnAction(e->handleMBtnDrop(e));
		mBtnOut.setOnAction(e->{
			StackPane root = (StackPane) mBtnOut.getScene().getRoot();
			root.getChildren().remove(scMain);
		});
	}
	
	public void handleMBtnPass(ActionEvent event) {
		Stage stage = new Stage(StageStyle.UTILITY);	// 비밀번호 변경 창
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(primaryStage);
		stage.setTitle("비밀번호 변경");

			try {
				Parent changePass = FXMLLoader.load(getClass().getResource("changePass.fxml"));
				TextField mem_pass = (TextField) changePass.lookup("#changePass");
				Button changeBtnPass = (Button) changePass.lookup("#changeBtnPass");
				changeBtnPass.setOnAction(e2->{
					try {
						conn = DBConn.getConnection();
						String sql = "UPDATE sc_member set mem_pass=PASSWORD(?) where mem_userid=?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, mem_pass.getText());
						pstmt.setString(2, rc.userid);
						int result = pstmt.executeUpdate();
						if(result >= 1) {
							Stage dialog = new Stage(StageStyle.UTILITY);	// 확인 창
							dialog.initModality(Modality.WINDOW_MODAL);
							dialog.initOwner(primaryStage);
							dialog.setTitle("비밀번호 변경.");
							
							Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
							ImageView imageView = (ImageView) check.lookup("#imageView");
							imageView.setImage(new Image(getClass().getResource("smile.png").toString()));
							Button btnOk = (Button) check.lookup("#btnOk");
							btnOk.setOnAction(e3->{
								dialog.close();
								stage.close();
							});
							Label txtTitle = (Label) check.lookup("#txtTitle");
							txtTitle.setText("비밀번호가 변경되었습니다.");
							
							Scene scene = new Scene(check);
							
							dialog.setScene(scene);
							dialog.setResizable(false);
							dialog.show();
						}
					} catch (SQLException | IOException | ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				});
				Scene scene = new Scene(changePass);
				
				stage.setScene(scene);
				stage.setResizable(false);
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	public void handleMBtnPh(ActionEvent event) {
		Stage stage = new Stage(StageStyle.UTILITY);	// 전화번호 변경 창	
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(primaryStage);
		stage.setTitle("전화번호 변경");
		
		try {
			Parent changePh = FXMLLoader.load(getClass().getResource("changePh.fxml"));
			TextField Cmem_ph = (TextField) changePh.lookup("#changePh");
			Button btnPhDup = (Button) changePh.lookup("#btnPhDup");
			Label txtPhDup = (Label) changePh.lookup("#txtPhDup");
			btnPhDup.setOnAction(e2->{
				try {
					conn = DBConn.getConnection();
					String sql = "SELECT mem_idx FROM sc_member WHERE mem_ph=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, Cmem_ph.getText());
					rs = pstmt.executeQuery();
					if(rs.next()) {
						txtPhDup.setText("이미 사용중인 전화번호입니다.");
					}else txtPhDup.setText("사용 가능한 전화번호입니다.");
				}catch(Exception exception) {
					exception.printStackTrace();
				}
			});
			Button changeBtnPh = (Button) changePh.lookup("#changeBtnPh");
			changeBtnPh.setOnAction(e2->{
				try {
					String sql = "UPDATE sc_member set mem_ph=? where mem_userid=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, Cmem_ph.getText());
					pstmt.setString(2, mem_userid);
					int result = pstmt.executeUpdate();
					if(result >= 1) {
						mem_ph = Cmem_ph.getText();
						Stage dialog = new Stage(StageStyle.UTILITY);	// 확인 창
						dialog.initModality(Modality.WINDOW_MODAL);
						dialog.initOwner(primaryStage);
						dialog.setTitle("전화번호 변경.");
						
						Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
						ImageView imageView = (ImageView) check.lookup("#imageView");
						imageView.setImage(new Image(getClass().getResource("smile.png").toString()));
						Button btnOk = (Button) check.lookup("#btnOk");
						btnOk.setOnAction(e3->{
							mPh.setText(mem_ph);
							dialog.close();
							stage.close();
						});
						Label txtTitle = (Label) check.lookup("#txtTitle");
						txtTitle.setText("전화번호가 변경되었습니다.");
						
						Scene scene = new Scene(check);
						
						dialog.setScene(scene);
						dialog.setResizable(false);
						dialog.show();
					}
				} catch (SQLException | IOException e1) {
					e1.printStackTrace();
				}
			});
			
			Scene scene = new Scene(changePh);
			
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void handleMBtnEmail(ActionEvent event) {
		Stage stage = new Stage(StageStyle.UTILITY);	// 이메일 변경 창
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(primaryStage);
		stage.setTitle("이메일 변경");
		
		try {
			Parent changeEmail = FXMLLoader.load(getClass().getResource("changeEmail.fxml"));
			TextField Cmem_email = (TextField) changeEmail.lookup("#changeEmail");
			Button changeBtnEmail = (Button) changeEmail.lookup("#changeBtnEmail");
			changeBtnEmail.setOnAction(e2->{
				try {
					conn = DBConn.getConnection();
					String sql = "UPDATE sc_member set mem_email=? where mem_userid=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, Cmem_email.getText());
					pstmt.setString(2, mem_userid);
					int result = pstmt.executeUpdate();
					if(result >= 1) {
						mem_email = Cmem_email.getText();
						Stage dialog = new Stage(StageStyle.UTILITY);	// 확인 창
						dialog.initModality(Modality.WINDOW_MODAL);
						dialog.initOwner(primaryStage);
						dialog.setTitle("이메일 변경.");
						
						Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
						ImageView imageView = (ImageView) check.lookup("#imageView");
						imageView.setImage(new Image(getClass().getResource("smile.png").toString()));
						Button btnOk = (Button) check.lookup("#btnOk");
						btnOk.setOnAction(e3->{
							mEmail.setText(mem_email);
							dialog.close();
							stage.close();
						});
						Label txtTitle = (Label) check.lookup("#txtTitle");
						txtTitle.setText("이메일이 변경되었습니다.");
						
						Scene scene = new Scene(check);
						
						dialog.setScene(scene);
						dialog.setResizable(false);
						dialog.show();
					}
				} catch (SQLException | IOException | ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			});
			
			Scene scene = new Scene(changeEmail);
			
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void handleMBtnDrop(ActionEvent event) {
		Stage stage = new Stage(StageStyle.UTILITY);	// 탈퇴 창
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(primaryStage);
		stage.setTitle("탈퇴하기");
		
		try {
			conn = DBConn.getConnection();

			Parent dropMem = FXMLLoader.load(getClass().getResource("dropMem.fxml"));
			PasswordField mem_pass = (PasswordField) dropMem.lookup("#dropPass");
			Label labelPass = (Label) dropMem.lookup("#labelPass");
			Button dropBtnMem = (Button) dropMem.lookup("#dropBtnMem");
			dropBtnMem.setOnAction(e->{
				try {
					String sql = "SELECT mem_idx FROM sc_member WHERE mem_userid=? AND mem_pass=PASSWORD(?)";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, mem_userid);
					pstmt.setString(2, mem_pass.getText());
					rs = pstmt.executeQuery();
					if(rs.next()) {
						String sql2 = "DELETE FROM sc_member WHERE mem_userid=? AND mem_pass=PASSWORD(?)";
						pstmt = conn.prepareStatement(sql2);
						pstmt.setString(1, mem_userid);
						pstmt.setString(2, mem_pass.getText());
						int result = pstmt.executeUpdate();
						if(result >= 1) {
							Stage dialog = new Stage(StageStyle.UTILITY);	// 확인 창
							dialog.initModality(Modality.WINDOW_MODAL);
							dialog.initOwner(primaryStage);
							dialog.setTitle("탈퇴완료");
							
							Parent check = FXMLLoader.load(getClass().getResource("check.fxml"));
							ImageView imageView = (ImageView) check.lookup("#imageView");
							imageView.setImage(new Image(getClass().getResource("sad.png").toString()));
							Button btnOk = (Button) check.lookup("#btnOk");
							btnOk.setOnAction(e3->{
								dialog.close();
								stage.close();
								StackPane root = (StackPane) mBtnDrop.getScene().getRoot();
								root.getChildren().remove(scMain);
							});
							Label txtTitle = (Label) check.lookup("#txtTitle");
							txtTitle.setText("탈퇴되었습니다.");
							
							Scene scene = new Scene(check);
							
							dialog.setScene(scene);
							dialog.setResizable(false);
							dialog.show();
						}
					}else labelPass.setText("비밀번호가 일치하지않습니다.");
				} catch (SQLException | IOException e1) {
					e1.printStackTrace();
				}
			});
			
			Scene scene = new Scene(dropMem);
			
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
			
		}catch (ClassNotFoundException | SQLException | IOException e1) {
			e1.printStackTrace();
		}
	}
}
