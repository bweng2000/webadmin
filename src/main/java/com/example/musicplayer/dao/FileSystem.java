package com.example.musicplayer.dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This is the data access class that directly talks to the file system
 * @author bweng
 * 
 */
public class FileSystem {
	
	private File fileDir;	//base file directory
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
	
	public FileSystem(String path) {
		fileDir = new File(path);
	}
	
	public File getFileDir() {
		return fileDir;
	}

	public void setFileDir(File fileDir) {
		this.fileDir = fileDir;
	}
	
	public String[] getFileList() {
		readLock.lock();
		try {
			return fileDir.list();
		} finally {
			readLock.unlock();
		}
	}
	
	public File[] getAllFiles() {
		return fileDir.listFiles();
	}
	
	public File[] getFilesByNames(String[] names) {
		readLock.lock();
		try {
			String baseDir = fileDir.getPath();
			if (!baseDir.endsWith("/")) {
				baseDir = baseDir.concat("/");
			}
			File[] files = new File[names.length];
			for (int i = 0; i < files.length; i++) {
				files[i] = new File(baseDir + names[i]);
			}
			// may need copy to a tmp file and return a tmp file to return since
			// the underlying file may be accessed by writer thread
			return files;
		} finally {
			readLock.unlock();
		}
	}

	public File getFile(String name) {
		readLock.lock();
		try {
			String baseDir = fileDir.getPath();
			if (!baseDir.endsWith("/")) {
				baseDir = baseDir.concat("/");
			}
			File mediaFile = new File(baseDir + name);
			// may need copy to a tmp file and return a tmp file to return since
			// the underlying file may be accessed by writer thread
			return mediaFile;
		} finally {
			readLock.unlock();
		}
	}
	
	public void sync(FileSystem src) {
		writeLock.lock();
		try {
			String[] filesDst = fileDir.list();
			String[] filesSrc = src.getFileDir().list();
			Set<String> dstFileNames = new HashSet<String>(
					Arrays.asList(filesDst));
			Set<String> srcFileNames = new HashSet<String>(
					Arrays.asList(filesSrc));
			Set<String> s1 = new HashSet<String>(srcFileNames); // copy the
																// source set to
																// s1
			Set<String> s2 = new HashSet<String>(dstFileNames); // copy the
																// destination
																// set to s2
			s2.removeAll(s1); // s2 is the set to be deleted
			s1.removeAll(dstFileNames); // s1 is the set to be copied

			// delete all files that are old in destination directory
			for (String filename : s2) {
				File fileToDelete = new File(fileDir, filename);
				fileToDelete.delete();
			}

			// copy all new files from source to destination directory
			for (String filename : s1) {
				File fileToCopy = new File(fileDir, filename);
				File fileFromSrc = new File(src.getFileDir(), filename);
				try {
					Files.copy(fileFromSrc.toPath(), fileToCopy.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

}
