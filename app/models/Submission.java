package models;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import com.avaje.ebean.Model;
		
    @Entity
    public class Submission extends Model {
          
    @Id 
    public Long submissionId;
	
    @Column
    @Constraints.Required
    public Long studentId;

    @Column
    @Constraints.Required
    public Long canvasAssignmentId;
	
    @ManyToOne
    Problem problem;

    public static Finder<Long, Submission> find = new Finder<Long, Submission>(
        Long.class, Submission.class);
        
	public Submission(){
	}
		
	public Long getSubmissionId() {
		return this.submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}

	public Problem getProblem() {
		return this.problem;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	public Long getStudentId(){
		return this.studentId;
	}

	public void setStudentId(Long studentId){
		this.studentId = studentId;
	}

	public Long getcanvasAssignmentId(){
		return this.canvasAssignmentId;
	}

	public void setcanvasAssignmentId(){
		this.canvasAssignmentId = canvasAssignmentId;
	}
}


