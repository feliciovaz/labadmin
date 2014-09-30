package pt.alu.lab;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class VirtualMachineAdmin
 */
@WebServlet("/vm")
public class VirtualMachineAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private LabDatabase database;
	private static final Logger LOG = LoggerFactory.getLogger(VirtualMachineAdmin.class);
	private static final String listVMsPageJsp = "/WEB-INF/jsp/view/listVms.jsp";
	private static final String createVMFormJsp = "/WEB-INF/jsp/view/createVMForm.jsp";
	private static final String modifyVMFormJsp = "/WEB-INF/jsp/view/modifyVMForm.jsp";
	private static final String operationStatusJsp = "/WEB-INF/jsp/view/operationStatus.jsp";
     	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VirtualMachineAdmin() {
        super();
        this.database = new LabDatabase("localhost", "aluipadmin","felicio", "alcatel" );
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LOG.info("Get request received");
		String operation = request.getParameter("operation");
		
		if (operation!=null && operation.equals("create")) {
			// show create VM form
			request.getRequestDispatcher(createVMFormJsp).forward(request, response);
			return;
		} else if (operation!=null && operation.equals("modify")) {
			String vmName = request.getParameter("vmName");
			showModifyForm(request, response, vmName);
		} else if (operation!=null && operation.equals("delrole")) {
			removeRoleFromVM(request, response);
		} else {
			listVMOperation(request, response);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String operation = request.getParameter("operation");
		if (operation!=null && operation.equals("create")) {
			createVMOperation(request, response);
		} else if (operation!=null && operation.equals("modify")) {
			modifyVMOperation(request, response);
		} else if (operation!=null && operation.equals("delete")) {
			deleteVMOperation(request, response);
		} else if (operation!=null && operation.equals("updrole")) {
			addRoleToVM(request, response);
		} else {
			listVMOperation(request, response);
		}
	}

	private void showModifyForm(HttpServletRequest request, HttpServletResponse response, String vmName) throws ServletException, IOException {
		VirtualMachine vm = database.getVirtualMachine(vmName);
		List<VirtualMachineRole> vmRoles = database.getVMRoles(vmName);
		if (vm!=null) {
			request.setAttribute("vm", vm);
			request.setAttribute("vmRoles", vmRoles);
			request.getRequestDispatcher(modifyVMFormJsp).forward(request, response);
			return;
		} else {
			// go to error page
			request.setAttribute("message", String.format("Failed to retrieve details for virtual machine %s, please contact the web administrator", vmName));
			request.setAttribute("backUrl", "vm");
			request.getRequestDispatcher(operationStatusJsp).forward(request, response);
			return;
		}		
	}

	private void addRoleToVM(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String vmName = request.getParameter("vmName");
		String vmRole = request.getParameter("vmRole");
		
		if (database.addRoleToVM(vmName, vmRole)) {
			showModifyForm(request, response, vmName);
			return;
		} else {
			// go to error page
			request.setAttribute("message", "Failed to add a role to virtual machine, please contact the web administrator");
			request.setAttribute("backUrl", "vm");
			request.getRequestDispatcher(operationStatusJsp).forward(request, response);
			return;
		}
	}
	
	private void removeRoleFromVM(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int roleId = Integer.parseInt(request.getParameter("roleId"));
		String vmName = request.getParameter("vmName");
		LOG.info("received request to delete role (id={}) for vm: {}", roleId, vmName); 
		if (database.delRoleFromVM(roleId)) {
			showModifyForm(request, response, vmName);
			return;
		} else {
			LOG.error("could not delete role id {} for virtual machine {}", roleId, vmName);
			// go to error page
			request.setAttribute("message", "Failed to add a role to virtual machine, please contact the web administrator");
			request.setAttribute("backUrl", "vm");
			request.getRequestDispatcher(operationStatusJsp).forward(request, response);
			return;
		}
	}
	
	private void listVMOperation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<VirtualMachine> virtualMachines = this.database.getVirtualMachines();
		
		request.setAttribute("virtualMachines", virtualMachines);
		request.getRequestDispatcher(listVMsPageJsp).forward(request, response);
		return;		
	}

	private void deleteVMOperation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOG.info("Post request received for deletion of VM");
		String vmName = request.getParameter("vmName");
		String status = database.deleteVM(vmName);
		if (!status.equals("ok")) {
			// delete IP in DB failed
			request.setAttribute("message", "Failed to delete IP address in the database, please contact the web administrator");
			request.setAttribute("details", status);
			request.setAttribute("backUrl", "vm");
			request.getRequestDispatcher(operationStatusJsp).forward(request, response);
			return;
		} else {
			// successful IP deletion
			request.setAttribute("message", "Succesfuly deleted VM: " + vmName);
			request.setAttribute("backUrl", "vm");
			request.getRequestDispatcher(operationStatusJsp).forward(request, response);
			return;
		}

	}
	
	private void modifyVMOperation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOG.info("Post request received for modifying VM");
		VirtualMachine vm = new VirtualMachine(request.getParameter("vmName"),
										request.getParameter("vmDescription"),
										request.getParameter("vmOS"),
										request.getParameter("vmOwner"));
		String status = database.modifyVM(vm);
		if (status.equals("ok")) {
			//request.setAttribute("backUrl", "vm");
			//request.setAttribute("message", "The Virtual Machine was modified successfuly: " + vm.getName());
			//request.getRequestDispatcher(operationStatusPageJsp).forward(request, response);
			response.sendRedirect("vm");
			return;
		} else {
			status = "Cannot modify virtual machine<br>" + status;
			request.setAttribute("status", status);
			request.getRequestDispatcher(modifyVMFormJsp).forward(request, response);
			return;
		}
	}
	
	private void createVMOperation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOG.info("Post request received for creating a new virtual machine");
		
		String newVmName = request.getParameter("name");
		String newVmDescription = request.getParameter("description");
		String newVmOS = request.getParameter("os");
		String newVmOwner = request.getParameter("owner");
		VirtualMachine newVm = new VirtualMachine(newVmName, newVmDescription, newVmOS, newVmOwner);
		String status = database.createVM(newVm);
		if (status.equals("ok")){
			// successful creation of VM
			request.setAttribute("backUrl", "vm");
			request.setAttribute("message", "The Virtual Machine was created successfuly: " + newVmName);
			request.getRequestDispatcher(operationStatusJsp).forward(request, response);
			return;
		} else {
			// failed to create VM
			String errorMsg = "Failed to create Virtual Machine: " + newVmName + "<br>" + status; 
			request.setAttribute("status", errorMsg);
			request.setAttribute("name", newVmName);
			request.setAttribute("description", newVmDescription);
			request.setAttribute("os", newVmOS);
			request.setAttribute("owner", newVmOwner);
			request.getRequestDispatcher(createVMFormJsp).forward(request, response);
			return;
		}
	}
}
