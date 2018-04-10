/**
 * Created on 24 באוק 2012 by Asaf Pinhassi
 */
package il.co.mobiledev.locationexample;

import android.content.Context;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Asaf Pinhassi
 *
 */
public abstract class PersistentObject<T> implements Serializable {

	private String mName = null;
	private String mDir = null;

	public PersistentObject() {
		this(null);
	}

	public PersistentObject(String name) {
		this(name,null);
	}
	public PersistentObject(String name, String dir) {
		mName = name;
		mDir = dir;
	}

	public synchronized boolean save() {
		return save(App.getInstance().getContext());
	}

	public synchronized void load() {
		load(App.getInstance().getContext());
	}

	public File getFile() {
		return getFile(App.getInstance().getContext());
	}
	
	public synchronized boolean save(Context context) {
		boolean res = false;

		ObjectOutputStream out = null;
		try {
			File file = getFile(context);
			if (file.exists())
				file.delete();
			FileOutputStream fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.flush();
			out.close();
			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return res;
	}

	public synchronized void load(Context context) {
		T obj = null;
		ObjectInputStream in = null;
		try {
			File file = getFile(context);
			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				in = new ObjectInputStream(fis);
				obj = (T) in.readObject();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		this.copy(obj);
		return;
	}

	public abstract void copy(T object);

	public String getFileName() {
		return (mName == null ? this.getClass().getSimpleName() : mName);
	}

	/**
	 * Deletes saved file
	 * @return true if file doesn't exist when finished
	 */
	public boolean deleteFile(Context context) {
		File file = getFile(context);
		try {
			if (file.exists())
				file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file.exists();
	}

	public File getFile(Context context){
		return new File(getFileDir(context) + File.separator + getFileName());
	}
	
	public File getDir(Context context){
		return new File(getFileDir(context));
	}

	/**
	 * @return the mDir
	 */
	public String getFileDir(Context context) {
		return (mDir != null ? mDir : context.getFilesDir().toString());
	}

	/**
	 * @param dir the mDir to set
	 */
	public void setFileDir(String dir) {
		this.mDir = dir;
	}
	
}
