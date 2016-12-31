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
import play.mvc.BodyParser;                     
import play.libs.Json.*;   
import play.libs.Jsonp;                     
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static play.libs.Json.toJson;
import play.Logger;
import play.mvc.*;
import models.*;
import views.html.*;

public class SubmissionController extends Controller {
	
	public Result addSubmission(Long problemID) {
		System.out.println("Result is received" );
		String score = request().getQueryString("score");
		System.out.println("Received score is:" + request().getQueryString("score"));
		Http.Cookie canvasAssignmentIdCookie = request().cookie("custom_canvas_assignment_id");
		Long assignmentId = Long.parseLong(canvasAssignmentIdCookie.value());
		System.out.println("AssignmentID is: " + assignmentId);
		Http.Cookie userIdCookie = request().cookie("custom_canvas_user_id");
		Long userId = Long.parseLong(userIdCookie.value());
		System.out.println("UserID is: " + userId);

		Problem problem = Problem.find.byId(problemID);
		System.out.println("Problem is: " + problem);
		
		List<Submission> submissions = Submission.find.where().eq("problem.problemId",problemID).eq("canvasAssignmentId",assignmentId).eq("studentId",userId).findList();
		System.out.println(submissions);
		if(submissions.size()==0){
			Submission submission = new Submission();
			submission.setcanvasAssignmentId(assignmentId);
			submission.setStudentId(userId);
			submission.setScore(score);
			submission.setProblem(problem);
			problem.getSubmissions().add(submission);
			submission.save();
		}
		else{
			submission.setScore(score);
			System.out.println("New score is added and the value is: "+ score);
		}
        String callback = request().getQueryString("callback");
		ObjectNode result = Json.newObject();
		result.put("received", true);
		result.put("score", request().getQueryString("score"));
		if (callback == null)
			return ok(result.asText());
		else
			return ok(Jsonp.jsonp(callback, result));	
	}

	public Result addSubmissions(){
	   JsonNode jsonPayload = request().body().asJson();
           Logger.info("json from client = {}", jsonPayload);

           Http.Cookie canvasAssignmentIdCookie = request().cookie("custom_canvas_assignment_id");
	   Long assignmentId = Long.parseLong(canvasAssignmentIdCookie.value());
	   System.out.println("AssignmentID is: " + assignmentId);
	   Http.Cookie userIdCookie = request().cookie("custom_canvas_user_id");
	   Long userId = Long.parseLong(userIdCookie.value());
	   System.out.println("UserID is: " + userId);
           Iterator<JsonNode> nodeIterator = jsonPayload.elements();
		
        while (nodeIterator.hasNext()) {
            JsonNode exercise = nodeIterator.next();
            Logger.info(exercise.toString());
	    if(exercise.has("activity")){
	    List<Submission> submissions = Submission.find.where().eq("canvasAssignmentId",assignmentId).eq("studentId",userId).eq("activity",exercise.get("activity").asText()).findList();
	    System.out.println(submissions);
		if(submissions.size()==0){
			Submission submission = new Submission();
			submission.setcanvasAssignmentId(assignmentId);
			submission.setStudentId(userId);
			submission.setActivity(exercise.get("activity").asText());
			submission.setScore(exercise.get("correct").asText()+"/"+exercise.get("maxscore").asText());
			submission.save();
		}
		else{
			submissions.get(0).setScore(exercise.get("correct").asText()+"/"+exercise.get("maxscore").asText());
			System.out.println("Solution is " +exercise.get("correct").asText()+"/"+exercise.get("maxscore").asText());
		}
      } 
}
	return ok();
}
}

