package com.topostechnology.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@Service
public class FtpsService {

	private static final Logger logger = LoggerFactory.getLogger(FtpsService.class);
	

	@Value("${ftp.152.url}")
	private String ftpUrl;

	@Value("${ftp.152.port}")
	public int port;

	@Value("${ftp.152.user}")
	private String user;

	@Value("${ftp.152.password}")
	private String password;

	@Value("${ftp.152.porting}")
	private String ftpFolderPortinf;

	@Value("${local.porting.folder}")
	private String localPortingFolder;

	public List<String> getNewPortingFiles() throws Exception{
		logger.info("Empieza proceso de descarga para  archivos de portabilidad");
		List<String> downloadedList = null;
			String remoteFolder ="/152/portabilidad"; 
			String localFolder="C:/sftp/portabilidad";
			this.createIfNotExistDirectory(localPortingFolder);
			downloadedList = downloadFromRemoteServer(remoteFolder, localFolder);
		return downloadedList;
	}
	
	public void process(String remoteFolder, String localFolder) throws IOException {
		logger.info("Empieza proceso de descarga para " + remoteFolder);
		this.createIfNotExistDirectory(localPortingFolder);
		List<String> addDownloadList = downloadFromRemoteServer(remoteFolder, localFolder);
		System.out.println("Archivos copiados"  + addDownloadList.size());
	}
	
	/**
	 * Se conecta a ftp y llama a metodo para descargar una carpeta remota auna
	 * carpeta local
	 * 
	 * @param remoteFolder
	 * @param localFolder
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> downloadFromRemoteServer(String remoteFolder, String localFolderPath) {
		logger.info("Conectando Ftp ... ");
		 File localFolder = new File(localFolderPath);
		List<String> filesAddList = new ArrayList<String>();
		JSch jsch = new JSch();
		Session session = null;
		ChannelSftp sftpChannel = null;
		try {
			session = jsch.getSession(user, ftpUrl, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			logger.info("Conectado :-)");
			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(remoteFolder);
			Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*.*");
			int countFiles = 0;
			System.out.println("");
			logger.info("Empezar a copiar carpeta " + remoteFolder +" a " + localFolderPath);
			// OBTIENE LISTA DE NOMBRES DE ARCHIVOS EN BD
			List<String> loadedLocalFiles = this.FolderFilesList(localFolder);
			for (ChannelSftp.LsEntry entry : list) {
				String fileName = entry.getFilename();
				Optional<String> founded = findFileName(loadedLocalFiles, fileName);
				// VALIDA, SOLO SI ES ARCHIVO NUEVO SE COPIA
				if (!founded.isPresent()) {
					logger.info("Archivo nuevo encontrado: " + fileName);
					sftpChannel.get(entry.getFilename(), localFolder +"/" + entry.getFilename());
					filesAddList.add(fileName);
					logger.info(" copiado");
					countFiles++;
				} else {
					logger.info(fileName + "Ya existe en la carpeta local");
				}
			}
			logger.info("Se copiaron " + countFiles + " archivos");
		} catch (Exception e) {
			logger.error("Se ha generado un error durante la descarga " + e.getMessage());
		} finally {
			sftpChannel.exit();
			if (session.isConnected()) {
				session.disconnect();
				logger.info("Desconectado");
			}
		}
		return filesAddList;
	}
	
	public List<String> FolderFilesList(final File carpeta) {
		List<String> filesNames= new ArrayList<String>();
	    for (final File ficheroEntrada : carpeta.listFiles()) {
	        	filesNames.add(ficheroEntrada.getName());
	    }
	    return filesNames;
	}

	private Optional<String> findFileName( List<String> loadedCrdFiles, String fileName) {
	    return loadedCrdFiles.stream()
	        .filter(s -> s.equals(fileName))
	        .findFirst();
	}
	
	private void createIfNotExistDirectory(String directoryName) throws IOException {
		File directory = new File(directoryName);
		if (!directory.exists()) {
			logger.info("Creando un nuevo directorio: " + directory.getCanonicalPath().toString()); //  TODO CAMBIAR  directory.getName()
			directory.mkdirs();
		}
	}
	
}
