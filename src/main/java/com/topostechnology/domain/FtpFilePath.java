//package com.topostechnology.domain;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "c_ftp_file_path")
//public class FtpFilePath extends CoreCatalogEntity{
//
//	private static final long serialVersionUID = -4789649969653338447L;
//	
//	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//	@Column(name ="download_path", nullable= false, unique = true)
//    private String downloadPath;
//    
//	@Column(name ="local_path", nullable= false, unique = true)
//    private String localPath;
//    
//    
//    private String description;
//
//
//}
//
