package controllers;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import play.*;
import play.data.*;
import play.libs.Json;
import static play.data.Form.*;
import java.net.*;
import java.io.*;

import play.Logger;
import play.mvc.*;
import models.*;

import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
	 
    public Result index() throws UnsupportedEncodingException{     
	Map<String, String[]> postParams = request().body().asFormUrlEncoded();

        for(String key: postParams.keySet())
    	    System.out.println(key + " - " + Arrays.toString(postParams.get(key)));
	    System.out.println();
if (postParams.get("lis_outcome_service_url") == null || postParams.get("lis_result_sourcedid") == null) {
            flash("warning", "");
	}
	else{
	response().setCookie(new Http.Cookie("lis_outcome_service_url", postParams.get("lis_outcome_service_url")[0],
                    null, null, null, false, false));
	response().setCookie(new Http.Cookie("lis_result_sourcedid", postParams.get("lis_result_sourcedid")[0],
                    null, null, null, false, false));
	response().setCookie(new Http.Cookie("custom_canvas_assignment_id", postParams.get("custom_canvas_assignment_id")[0],
                    null, null, null, false, false));
	response().setCookie(new Http.Cookie("custom_canvas_user_id", postParams.get("custom_canvas_user_id")[0],
                    null, null, null, false, false));
}
	 String url = controllers.routes.HomeController.getAssignment().url()
                + "?id=" + URLEncoder.encode(request().getQueryString("id"), "UTF-8");
        Logger.info(url);
        return redirect(url);
  }
  
	//Method to show the assignment landing page
	public Result createAssignment() {
        Map<String, String[]> postParams = request().body().asFormUrlEncoded();

	 for(String key: postParams.keySet())
    		System.out.println(key + " - " + Arrays.toString(postParams.get(key)));
	 System.out.println();
        response().setCookie(new Http.Cookie("launch_presentation_return_url", postParams.get("launch_presentation_return_url")[0],
                    null, null, null, false, false));
        System.out.println(postParams.get("launch_presentation_return_url")[0]);
        return ok(create_exercise.render());
    }
	
	//Method to save the created assignment
	public Result addAssignment() {        
		DynamicForm bindedForm = Form.form().bindFromRequest();
   		String problemlist = bindedForm.get("url");
		Assignment assignment = new Assignment();
		assignment.save();
        	System.out.println(problemlist);
		if(null != problemlist|| !problemlist.equals("")) {
			String [] problemArr = problemlist.split(","); 
			for(String problemstr : problemArr) {
				if(null != problemstr && !problemstr.equals("")) {
					Problem problem = new Problem();
					problem.setProblemUrl(problemstr);
					problem.setAssignment(assignment);
					assignment.getProblems().add(problem);
					problem.save();
				}	
			}
		
		}
		List<Problem> problems = Problem.find.fetch("assignment").where().eq("assignment.assignmentId",assignment.assignmentId).findList();
	
		Http.Cookie launchReturnUrlCookie = request().cookie("launch_presentation_return_url");
		String returnUrl = launchReturnUrlCookie.value();
		System.out.println("ReturnURL is: " + returnUrl);
	
		return ok(showassignment.render(returnUrl,assignment,problems));
	}
	
	public Result getAssignment(){
                System.out.println("ID is: "+ request().getQueryString("id"));
		Long assignmentId = Long.parseLong(request().getQueryString("id"));
		Assignment assignment = Assignment.find.byId(assignmentId);
		List<Problem> problems = Problem.find.fetch("assignment").where().eq("assignment.assignmentId",assignment.assignmentId).findList();
        	System.out.println(problems);
        	return ok(finalAssignment.render(problems));
	}	

	public Result saveEditedAssignment(Long assignment) {
        
		DynamicForm bindedForm = Form.form().bindFromRequest();
        	String problemlist = bindedForm.get("url");
		Assignment assignment1 = Assignment.find.byId(assignment);
        	System.out.println(problemlist);
		if(null != problemlist|| !problemlist.equals("")) {
			String [] problemArr = problemlist.split(","); 
			for(String problemstr : problemArr) {
				if(null != problemstr && !problemstr.equals("")) {
					Problem problem = new Problem();
					problem.setProblemUrl(problemstr);
					problem.setAssignment(assignment1);
					assignment1.getProblems().add(problem);
					problem.save();
				}	
			}
		assignment1.save();
		}
		List<Problem> problems = Problem.find.fetch("assignment").where().eq("assignment.assignmentId",assignment1.assignmentId).findList();
    		System.out.println(problems);
		Http.Cookie launchReturnUrlCookie = request().cookie("launch_presentation_return_url");
		String returnUrl = launchReturnUrlCookie.value();
		System.out.println("ReturnURL is: " + returnUrl);
        	return ok(showassignment.render(returnUrl,assignment1,problems));
	}
    

	public Result showEditPage(Long assignment) {
		Assignment assignment1 = Assignment.find.byId(assignment);
		System.out.println(assignment1.getAssignmentId());
        	List<Problem> problems = Problem.find.fetch("assignment").where().eq("assignment.assignmentId",assignment1.assignmentId).findList();
        	System.out.println(problems);
        	return ok(editAssignment.render(assignment1, problems));    
    }

}

