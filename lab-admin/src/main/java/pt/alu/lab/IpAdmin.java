package pt.alu.lab;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class IpAdmin
 */
public class IpAdmin extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final String hostname = "localhost";
	
	private static final String driverClass = "com.mysql.jdbc.Driver";
    private static final String jdbcConnection = "jdbc:mysql://" + hostname  + "/aluipadmin?" +
            "user=felicio&password=alcatel";
    
    private static final String selectIps = "select ipaddr, is_free, description from ip_addresses_tbl";
    private static final String selectOneIp = "select ipaddr, is_free, description from ip_addresses_tbl where ipaddr=?";
    
    private static final String createIpFormJsp = "/WEB-INF/jsp/view/createIpForm.jsp";
    private static final String modifyIpFormJsp = "/WEB-INF/jsp/view/modifyIpForm.jsp";
    private static final String listIpsJsp = "/WEB-INF/jsp/view/listIps.jsp";
    private static final String operationStatusJsp = "/WEB-INF/jsp/view/operationStatus.jsp";

    private Connection connection;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IpAdmin() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Check which operation to perform
		String operation = request.getParameter("operation");
		System.out.println("GET Operation is: " + operation);
		if (operation!=null && operation.equals("showCreateIpForm")) {
			
			// Create IP form
			
			request.setAttribute("firstByte", "159");
			request.setAttribute("secondByte", "23");
			request.setAttribute("thirdByte", "66");
			request.setAttribute("fourthByte", "");
			request.setAttribute("ipDescription", "");
			request.setAttribute("ipBusyChecked", "checked");
			
			request.getRequestDispatcher(createIpFormJsp).forward(request, response);
			return;
		} else if (operation!=null && operation.equals("showModifyIpForm")) {
			
			// Modify IP form
			
			String ipAddress = request.getParameter("ipAddress");
			request.setAttribute("ipAddress", ipAddress);
			request.setAttribute("ipDescription", request.getParameter("ipDescription"));
			if (request.getParameter("ipIsBeingUsed")!=null && request.getParameter("ipIsBeingUsed").equals(Boolean.TRUE.toString())) {
				request.setAttribute("ipIsBeingUsedChecked", "checked");				
			} else {
				request.setAttribute("ipIsBeingUsedChecked", "");
			}
			
			request.getRequestDispatcher(modifyIpFormJsp).forward(request, response);
			return;
			
		} else {
		
			// List IP addresses page
			
			List<IpAddress> allIps = getIpAddresses();
			Collections.sort(allIps);
			request.setAttribute("ipAddresses", allIps);
			request.getRequestDispatcher(listIpsJsp).forward(request, response);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Check which operation to perform
		String operation = request.getParameter("operation");
		System.out.println("POST Operation is: " + operation);
		if (operation!=null && operation.equals("modifyIp")) {
			
			// get parameters from modify form
			String ipAddress = request.getParameter("ipAddress");
			String ipDescription = request.getParameter("ipDescription");
			Boolean ipIsBeingUsed = Boolean.FALSE;
			if (request.getParameter("ipIsBeingUsed")!=null && request.getParameter("ipIsBeingUsed").equals("Allocated")) {
				ipIsBeingUsed = Boolean.TRUE;
			}

			// update IP in DB
			String status = updateIpInDb(new IpAddress(ipAddress, ipIsBeingUsed, ipDescription));
			if (status.isEmpty()) {
				// successful modification of IP
				//request.setAttribute("message", "Successfuly modified IP address: " + ipAddress);
				//request.getRequestDispatcher(successfulOperationJsp).forward(request, response);
				response.sendRedirect("ip");
				return;
			}
			
			// error in modification of IP
			request.setAttribute("status", status);
			request.setAttribute("ipAddress", ipAddress);
			request.setAttribute("ipDescription", ipDescription);
			if (ipIsBeingUsed) {
				request.setAttribute("ipIsBeingUsedChecked", "checked");
			} else {
				request.setAttribute("ipIsBeingUsedChecked", "");
			}
			request.getRequestDispatcher(modifyIpFormJsp).forward(request, response);
			return;
		} else if (operation!=null && operation.equals("deleteIp")) {
			String ipAddress = request.getParameter("ipAddress");
			String status = deleteIpInDb(ipAddress);
			if (!status.isEmpty()) {
				// delete IP in DB failed
				request.setAttribute("message", "Failed to delete IP address in the database, please contact the web administrator");
				request.setAttribute("details", status);
				request.setAttribute("backUrl", "ip");
				request.getRequestDispatcher(operationStatusJsp).forward(request, response);
				return;
			} else {
				// successful IP deletion
				request.setAttribute("message", "Succesfuly deleted IP: " + ipAddress);
				request.setAttribute("backUrl", "ip");
				request.getRequestDispatcher(operationStatusJsp).forward(request, response);
				return;
			}
		}
		
		// get parameters from creation form
		String newIpAddressFirstByte = request.getParameter("first-byte");
		String newIpAddressSecondByte = request.getParameter("second-byte");
		String newIpAddressThirdByte = request.getParameter("third-byte");
		String newIpAddressFourthByte = request.getParameter("fourth-byte");
		String newIpAddressDescription = request.getParameter("ip-description");
		String newIpIsBusy = request.getParameter("ip-is-busy");
		
		// set attributes in case the form has to re-rendered in case of an error occurs
		request.setAttribute("firstByte", newIpAddressFirstByte);
		request.setAttribute("secondByte", newIpAddressSecondByte);
		request.setAttribute("thirdByte", newIpAddressThirdByte);
		request.setAttribute("fourthByte", newIpAddressFourthByte);
		request.setAttribute("ipDescription", newIpAddressDescription);
		if (newIpIsBusy!=null && newIpIsBusy.equals("Allocated")) {
			request.setAttribute("ipBusyChecked", "checked");
		} else {
			request.setAttribute("ipBusyChecked", "");
		}
	
		String newIpStatus = validateNewIpAddress(newIpAddressFirstByte, newIpAddressSecondByte, newIpAddressThirdByte, newIpAddressFourthByte, newIpAddressDescription); 
		if (!newIpStatus.isEmpty()) { 
			// there was an error validating the new ip address so go back to the form and print the error message
			request.setAttribute("status", newIpStatus);
			request.getRequestDispatcher(createIpFormJsp).forward(request, response);
			return;
		}
		
		// check whether IP address already exists in the DB
		String newIpAddress = newIpAddressFirstByte + "." + newIpAddressSecondByte + "." + newIpAddressThirdByte + "." + newIpAddressFourthByte;
		if (ipExistsInDb(newIpAddress)) {
			request.setAttribute("status", "This IP address already exists.");
			request.getRequestDispatcher(createIpFormJsp).forward(request, response);
			return;
		}
		
		// compute isBusy flag to be provide to createIpAddress()
		Boolean isBusy = getBusyValue(newIpIsBusy);
				
		// create the new IP address
		if (!createNewIpInDb(newIpAddress, newIpAddressDescription, isBusy)) {
			request.setAttribute("status", "Internal error: could not create IP address in database, please contact the web administrator.");
			request.getRequestDispatcher(createIpFormJsp).forward(request, response);
			return;
		}
		
		// successfuly craeted new IP address
		request.setAttribute("message", "Successfuly created IP address: " + newIpAddress);
		request.setAttribute("backUrl", "ip");
		request.getRequestDispatcher(operationStatusJsp).forward(request, response);
		return;
	}

	/*
	 * Always returns a list (even though it might be empty)
	 */
	private List<IpAddress> getIpAddresses() {
		List<IpAddress> allIps = null;
		
		// open the database connection
		if (openDb() != null) {
			allIps = getIpAddressesFromDb();
		} else {
			allIps = new ArrayList<>();
		}
		return allIps;
	}
	
	private Boolean getBusyValue(String fromValue) {
		if (fromValue!=null && fromValue.equals("Allocated")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	private String validateNewIpAddress(String firstByte, String secondByte, String thirdByte, String fourthByte, String description) {
		// validate bytes
		String bytesStatus = validateIpBytes(firstByte, secondByte, thirdByte, fourthByte);
		if (!bytesStatus.isEmpty())
			return bytesStatus;

		return ""; // success status message
	}
	
	private String validateIpBytes(String firstByte, String secondByte, String thirdByte, String fourthByte) {
		if (!validByte(firstByte)) {
			return "Invalid first byte of new IP address.";
		}
		if (!validByte(secondByte)) {
			return "Invalid second byte of new IP address.";
		}
		if (!validByte(thirdByte)) {
			return "Invalid third byte of new IP address.";
		}
		if (!validByte(fourthByte)) {
			return "Invalid fourth byte of new IP address.";
		}
		return "";
	}
	
	private Boolean validByte(String aByteStr) {
		if (aByteStr == null || aByteStr.isEmpty()) {
			return Boolean.FALSE;
		}
		return Integer.valueOf(aByteStr) != null;	
	}
	
	private Connection openDb() {
		try {
			if (this.connection==null || !this.connection.isValid(5)) {
				Class.forName(driverClass);
				this.connection = DriverManager.getConnection(IpAdmin.jdbcConnection);
			}
		} catch(SQLException|ClassNotFoundException ex) {
			System.out.println("ERROR: cannot open connection to database: " + ex.getMessage());
			this.connection = null;
		}
		return this.connection;
	}

	private Boolean createNewIpInDb(String newIpAddress,
			String newIpAddressDescription, Boolean newIpAddressIsBusy) {
		Boolean status = Boolean.FALSE;
		
		if (openDb() == null)
			return Boolean.FALSE;
		
		String createIpStatement = "insert into ip_addresses_tbl (ipaddr, description, is_free) values(?, ?, ?)";
		CallableStatement stmt = null;
	
		try {
			stmt = this.connection.prepareCall(createIpStatement);
			stmt.setString(1, newIpAddress);
			stmt.setString(2, newIpAddressDescription);
			stmt.setBoolean(3, !newIpAddressIsBusy);
			stmt.executeUpdate();
			status = Boolean.TRUE;
		} catch (SQLException ex) {
			System.out.println("ERROR: executing sql statement: " + createIpStatement);
			System.out.println("ERROR: details are: " + ex.getMessage());
		} finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		}
	
		return status;
	}

	private String updateIpInDb(IpAddress ip) {
		String status = "";
		
		if (openDb() == null)
			return "Cannot access the database. Please contact your web administrator.";
		
		String modifyIpStatement = "update ip_addresses_tbl set description=?, is_free=? where ipaddr=?";
		CallableStatement stmt = null;
	
		try {
			stmt = this.connection.prepareCall(modifyIpStatement);
			stmt.setString(1, ip.getDescription());
			stmt.setBoolean(2, !ip.getIsBeingUsed());
			stmt.setString(3, ip.getIp());
			stmt.executeUpdate();
			status = "";
		} catch (SQLException ex) {
			System.out.println("ERROR: executing sql statement: " + modifyIpStatement);
			System.out.println("ERROR: details are: " + ex.getMessage());
		} finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		}
	
		return status;
	}

	private String deleteIpInDb(String ip) {
		String status = "";
		
		if (openDb() == null)
			return "Cannot access the database. Please contact the web administrator.";
		
		String deleteIpStatement = "delete from ip_addresses_tbl where ipaddr=?";
		CallableStatement stmt = null;
	
		try {
			stmt = this.connection.prepareCall(deleteIpStatement);
			stmt.setString(1, ip);
			stmt.executeUpdate();
			status = "";
		} catch (SQLException ex) {
			System.out.println("ERROR: executing sql statement: " + deleteIpStatement);
			System.out.println("ERROR: details are: " + ex.getMessage());
		} finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		}
	
		return status;
	}

	private List<IpAddress> getIpAddressesFromDb() {
		List<IpAddress> retrievedIps = new ArrayList<>();
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = this.connection.createStatement();
			rs = stmt.executeQuery(selectIps);
			while (rs.next()) {
				retrievedIps.add(new IpAddress(rs.getString(1), !rs.getBoolean(2), rs.getString(3)));
			}
		} catch (SQLException ex) {
			System.out.println("ERROR: executing sql statement: " + selectIps);
			System.out.println("ERROR: details are: " + ex.getMessage());
		} finally {
			if (rs!=null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
			rs = null;
			
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		}
		return retrievedIps;
	}

	private Boolean ipExistsInDb(String ip) {
		if (openDb() == null)
			return Boolean.TRUE;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;		
		Boolean ipExists = Boolean.FALSE;

		try {
			stmt = this.connection.prepareStatement(selectOneIp);
			stmt.setString(1, ip);
			ipExists = Boolean.FALSE;
			rs = stmt.executeQuery();
			while (rs.next()) {
				ipExists = Boolean.TRUE;
			}
		} catch (SQLException ex) {
			System.out.println("ERROR: executing sql statement: " + selectOneIp);
			System.out.println("ERROR: details are: " + ex.getMessage());
		} finally {
			if (rs!=null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
			rs = null;
			
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		}
		
		return ipExists;
	}
}
