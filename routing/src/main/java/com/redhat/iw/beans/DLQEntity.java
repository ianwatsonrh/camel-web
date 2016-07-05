package com.redhat.iw.beans;

import javax.persistence.*;

import org.apache.camel.component.jpa.Consumed;

@Entity
@Table(name = "USECASE.T_ERROR")
@NamedQuery(name="consume", query="SELECT OBJECT(e) FROM DLQEntity e where e.status = 'FIXED'") 
public class DLQEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;
	
	@Column(name = "ERROR_MESSAGE")
	private String errorMessage;
	
    private String message;
    private String status;
    
    public DLQEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Consumed
	public void consumed() {
		setStatus("CLOSE");
	}

}
