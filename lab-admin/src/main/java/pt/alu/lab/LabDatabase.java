package pt.alu.lab;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabDatabase {

	private static final Logger LOG = LoggerFactory.getLogger(LabDatabase.class);
	
	private Connection connection;
	
	private static final String driverClass = "com.mysql.jdbc.Driver";
    private final String jdbcConnection;
    
	LabDatabase(String host, String mysqlDb, String user, String pass) {
		jdbcConnection = String.format("jdbc:mysql://%s/%s?user=%s&password=%s", host, mysqlDb, user, pass);
	}

	public Boolean addRoleToVM(String vmName, String vmRole) {
		Boolean status = Boolean.FALSE;
		String createRoleStatement = "insert into virtual_machines_roles_tbl (role, vm_name) values(?, ?)";
		CallableStatement stmt = null;
		try {
			openDb();		
			stmt = this.connection.prepareCall(createRoleStatement);
			stmt.setString(1, vmRole);
			stmt.setString(2, vmName);
			stmt.executeUpdate();
			status = Boolean.TRUE;
		} catch (SQLException ex) {
			LOG.error("SQL exception executing sql statement: {}", createRoleStatement);
			LOG.error(ex.getMessage());
		} catch (Exception ex) {
			LOG.error("Unknown exception executing sql statement: {}", createRoleStatement);
			LOG.error(ex.getMessage());
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
	
	public String createVM(VirtualMachine vm) {
		String createVMStatement = "insert into virtual_machines_tbl (name, description, os, owner) values(?, ?, ?, ?)";
		CallableStatement stmt = null;
		String status = "ok";
		try {
			openDb();		
			stmt = this.connection.prepareCall(createVMStatement);
			stmt.setString(1, vm.getName());
			stmt.setString(2, vm.getDescription());
			stmt.setString(3, vm.getOs());
			stmt.setString(4, vm.getOwner());
			stmt.executeUpdate();
		} catch (SQLException ex) {
			LOG.error("executing sql statement: {}", createVMStatement);
			LOG.error(ex.getMessage());
			status = ex.getMessage();
		} catch (Exception ex) {
			status = ex.getMessage();
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
	
	public String modifyVM(VirtualMachine vm) {
		String modifyVMStatement = "update virtual_machines_tbl set description=?, os=?, owner=? where name=?";
		CallableStatement stmt = null;
		String status = "ok";
		try {
			
			openDb();
			
		
			stmt = this.connection.prepareCall(modifyVMStatement);
			stmt.setString(4, vm.getName());
			stmt.setString(1, vm.getDescription());
			stmt.setString(2, vm.getOs());
			stmt.setString(3, vm.getOwner());
			stmt.executeUpdate();
		} catch (SQLException ex) {
			LOG.error("executing sql statement: {}", modifyVMStatement);
			LOG.error(ex.getMessage());
			status = ex.getMessage();
		} catch (Exception ex) {
			status = ex.getMessage();
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
	
	public Boolean delRoleFromVM(int roleId) {
		Boolean status = Boolean.FALSE;
		String deleteRoleStatement = "delete from virtual_machines_roles_tbl where id=?";
		CallableStatement stmt = null;
		boolean dbOpenFlag = false;
		try {
			
			openDb();
			dbOpenFlag = true; // no exception opening the DB
			
			stmt = this.connection.prepareCall(deleteRoleStatement);
			stmt.setInt(1, roleId);
			stmt.executeUpdate();
			status = Boolean.TRUE;
		} catch (Exception ex) {
			LOG.error("could not delete role id: {}", roleId);
			if (dbOpenFlag) {
				LOG.error("executing sql statement: {}", deleteRoleStatement);
				LOG.error(ex.getMessage());
			}
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
	
	public String deleteVM(String vmName) {
		String deleteVMStatement = "delete from virtual_machines_tbl where name=?";
		CallableStatement stmt = null;
		String status = "ok";
		try {
			
			openDb();
			
		
			stmt = this.connection.prepareCall(deleteVMStatement);
			stmt.setString(1, vmName);
			stmt.executeUpdate();
		} catch (SQLException ex) {
			LOG.error("executing sql statement: {}", deleteVMStatement);
			LOG.error(ex.getMessage());
			status = ex.getMessage();
		} catch (Exception ex) {
			status = ex.getMessage();
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
	
	public List<VirtualMachineRole> getVMRoles(String vmName) {
		List<VirtualMachineRole> roles = new ArrayList<VirtualMachineRole>();
		String query = "select id, role from virtual_machines_roles_tbl where vm_name=?";
		CallableStatement statement=null;
		ResultSet resultSet=null;
		
		try {
			statement = connection.prepareCall(query);
			statement.setString(1, vmName);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				roles.add(new VirtualMachineRole(resultSet.getInt(1), resultSet.getString(2), vmName));
			}
		} catch (SQLException ex) {
			LOG.error("Error retrieving roles for virtual machine: {}", vmName);
			LOG.error("Error executing sql statement: {}", statement);
			LOG.error(ex.getMessage());
		} catch (Exception ex) {
			LOG.error("Error retrieving roles for virtual machine: {}", vmName);
			LOG.error(ex.getMessage());
		} finally {
			try {
				if (resultSet!=null) {
					resultSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch(Exception ex) {
				
			} finally {
				resultSet = null;
				statement = null;
			}
		}
		return roles;
	}
	
	public List<VirtualMachine> getVirtualMachines() {
		List<VirtualMachine> virtualMachines = new ArrayList<VirtualMachine>();
		String query = "select name, description, os, owner from virtual_machines_tbl";
		Statement statement=null;
		ResultSet resultSet=null;

		try {
			//getElements(query, statement, resultSet);
			openDb();
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				virtualMachines.add(new VirtualMachine(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4)));
			}
		} catch (Exception ex) {
			LOG.error("Error retrieving virtual machines from the database. Details follow...");
			LOG.error(ex.getMessage());
		} finally {
			try {
				if (resultSet!=null) {
					resultSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch(Exception ex) {
				
			} finally {
				resultSet = null;
				statement = null;
			}			
		}
		//closeResultSetAndStatement(statement, resultSet);
		return virtualMachines;
	}
	
	public VirtualMachine getVirtualMachine(String vmName) {
		VirtualMachine vm = null;
		String query = "select name, description, os, owner from virtual_machines_tbl where name=?";
		CallableStatement statement=null;
		ResultSet resultSet=null;

		try {
			openDb();
			statement = this.connection.prepareCall(query);
			statement.setString(1, vmName);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				vm = new VirtualMachine(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
			}
		} catch (Exception ex) {
			LOG.error("Error retrieving virtual machine from the database: {}", vmName);
			LOG.error(ex.getMessage());
		} finally {
			try {
				if (resultSet!=null) {
					resultSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch(Exception ex) {
				
			} finally {
				resultSet = null;
				statement = null;
			}			
		}
		return vm;
	}
	
	private void openDb() throws Exception {
		try {
			if (this.connection==null || !this.connection.isValid(5)) {
				Class.forName(driverClass);
				this.connection = DriverManager.getConnection(jdbcConnection);
			}
		} catch(SQLException|ClassNotFoundException ex) {
			LOG.error("ERROR: cannot open connection to database: {}", ex.getMessage());
			this.connection = null;
			throw ex;
		}
	}

}
