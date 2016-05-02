package mn.odi.bonaqua.rest;

import java.io.File;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.*;

@Path("/bonaqua")
public class ChallengeService {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	static final String DB_URL = "jdbc:mysql://localhost/bonaqua";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "";

	Connection conn = null;
	Statement stmt = null;

	@GET
	@Path("registeruser")
	public Response registerUser(@QueryParam("f_id") String f_id, @QueryParam("f_name") String f_name,
			@QueryParam("f_link") String f_link, @QueryParam("username") String username) {
		System.out.println("f_id " + f_id);
		System.out.println("f_name " + f_name);
		System.out.println("f_link " + f_link);
		System.out.println("username " + username);

		String ack = "1";

		if (f_id == null || f_name == null || f_link == null || username == null) {
			ack = "0";
		} else {
			try {
				connect();

				// Get User ID;
				String sql_user = "SELECT COUNT(*) AS rowcount FROM user where f_id = " + f_id + ";";

				System.out.println(sql_user);

				ResultSet rs_user = stmt.executeQuery(sql_user);
				rs_user.next();
				int count = rs_user.getInt("rowcount");
				rs_user.close();
				if (count == 0) {
					String sql_insert_device = "INSERT INTO user (f_id,f_name,f_link,username) VALUES ('" + f_id
							+ "', '" + f_name + "', '" + f_link + "', '" + username + "')";
					System.out.println(sql_insert_device);
					stmt.executeUpdate(sql_insert_device);
				}
				// close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return Response.status(200).entity(ack).build();
	}

	@GET
	@Path("challenge")
	public Response createChallenge(@QueryParam("f_id") String f_id, @QueryParam("cKal") String cKal,
			@QueryParam("duration") String duration, @QueryParam("date") String date) {
		System.out.println("f_id " + f_id);
		System.out.println("cKal " + cKal);
		System.out.println("duration " + duration);
		System.out.println("date " + date);

		String ack = "1";

		if (f_id == null || cKal == null || duration == null || date == null) {
			ack = "0";
		} else {

			try {

				// Get Challenge ID;
				/*
				 * String sql_challenge =
				 * "SELECT COUNT(*) AS rowcount FROM challenge where f_id = "
				 * +f_id+" and date = "+date; ResultSet rs_challenge =
				 * stmt.executeQuery(sql_challenge); rs_challenge.next(); int
				 * count = rs_challenge.getInt("rowcount"); System.out.println(
				 * "count "+count); rs_challenge.close(); if (count == 0) {
				 */
				String sql_insert_device = "INSERT INTO challenge (f_id,cKal,duration) VALUES (" + f_id + ", "
						+ cKal + ", " + duration + ")";
				System.out.println(sql_insert_device);
				connect();
				stmt.executeUpdate(sql_insert_device);
				// }
				close();

			} catch (Exception se) {
			      System.err.println(se.getMessage());
			}

		}
		return Response.status(200).entity(ack).build();
	}

	@GET
	@Path("leaderboard")
	public Response getLeaderBoard() {
		String ack = "";
		
		try {
			connect();

			// Get User ID;
			String sql_user = "SELECT *,user.f_name as f_name FROM challenge INNER JOIN user ON challenge.f_id =user.f_id ORDER BY cKal DESC, duration ASC";
			JSONArray list = new JSONArray();

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql_user);
			// iterate through the java resultset
			while (rs.next()) {
				int id = rs.getInt("id");
				String f_name = rs.getString("f_name");
				String cKal = rs.getString("cKal");
				String duration = rs.getString("duration");
				String date = rs.getString("date");
				JSONObject obj = new JSONObject();
				obj.put("f_name", f_name);
				obj.put("cKal", cKal);
				obj.put("duration", duration);
				obj.put("date", date);
					
				list.put(obj);
				
				// System.out.println("f_id "+f_id+" cKal "+cKal+" duration
				// "+duration+" date "+date);
			}

			StringWriter out = new StringWriter();
			list.write(out);

			ack = out.toString();
			System.out.println("ack " + ack);

			st.close();
			// close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return Response.status(200).entity(ack).build();
	}

	
	@GET
	@Path("create_comment")
	public Response createComment(@QueryParam("f_id") String f_id, @QueryParam("comment") String comment) {
		System.out.println("f_id " + f_id);
		System.out.println("comment " + comment);

		String ack = "1";

		if (f_id == null || comment == null) {
			ack = "0";
		} else {

			try {

				// Get Challenge ID;
				/*
				 * String sql_challenge =
				 * "SELECT COUNT(*) AS rowcount FROM challenge where f_id = "
				 * +f_id+" and date = "+date; ResultSet rs_challenge =
				 * stmt.executeQuery(sql_challenge); rs_challenge.next(); int
				 * count = rs_challenge.getInt("rowcount"); System.out.println(
				 * "count "+count); rs_challenge.close(); if (count == 0) {
				 */
				String sql_insert_comment = "INSERT INTO comment (f_id,comment) VALUES (" + f_id + ", "
						+ comment+ ")";
				System.out.println(sql_insert_comment);
				connect();
				stmt.executeUpdate(sql_insert_comment);
				// }
				close();

			} catch (Exception se) {
			      System.err.println(se.getMessage());
			}

		}
		return Response.status(200).entity(ack).build();
	}
	
	@GET
	@Path("read_comment")
	public Response getComment() {
		String ack = "";
		
		try {
			connect();

			// Get User ID;
			String sql_user = "SELECT * from comment";
			JSONArray list = new JSONArray();

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql_user);
			// iterate through the java resultset
			while (rs.next()) {
				int id = rs.getInt("id");
				String f_name = rs.getString("f_name");
				String cKal = rs.getString("cKal");				
				String duration = rs.getString("duration");
				String date = rs.getString("date");
				JSONObject obj = new JSONObject();
				obj.put("f_name", f_name);
				obj.put("cKal", cKal);
				obj.put("duration", duration);
				obj.put("date", date);
					
				list.put(obj);
				
				// System.out.println("f_id "+f_id+" cKal "+cKal+" duration
				// "+duration+" date "+date);
			}

			StringWriter out = new StringWriter();
			list.write(out);

			ack = out.toString();
			System.out.println("ack " + ack);

			st.close();
			// close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return Response.status(200).entity(ack).build();
	}
	
	
	
	// Connect to the database
	private void connect() throws SQLException, ClassNotFoundException {

		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);

		System.out.println("Creating statement...");
		stmt = conn.createStatement();
	}

	// Close database
	private void close() {
		try {
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Success!");
	}

	// Get Server IP
	private String getIpAddress() {

		String ipAddress = null;

		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

			while (networkInterfaces.hasMoreElements()) {

				NetworkInterface networkInterface = networkInterfaces.nextElement();

				byte[] hardwareAddress = networkInterface.getHardwareAddress();
				if (null == hardwareAddress || 0 == hardwareAddress.length
						|| (0 == hardwareAddress[0] && 0 == hardwareAddress[1] && 0 == hardwareAddress[2]))
					continue;

				Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

				if (inetAddresses.hasMoreElements())
					ipAddress = inetAddresses.nextElement().toString();

				break;
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return ipAddress;
	}

}