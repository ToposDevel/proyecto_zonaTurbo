package com.topostechnology.rest.client.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachFile implements Serializable {
	
	private static final long serialVersionUID = -7454993909881034770L;
	private String filePath;
	private String fileName;
	private byte[] file;
}
