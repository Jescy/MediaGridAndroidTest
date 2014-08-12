package com.dismantle.mediagrid.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;

import com.dismantle.mediagrid.ChatFragment;
import com.dismantle.mediagrid.CouchDB;
import com.dismantle.mediagrid.GlobalUtil;
import com.dismantle.mediagrid.HttpService;

import junit.framework.TestCase;

public class TestCouchDB extends TestCase {

	protected void setUp() throws Exception {
		HttpService.getInstance().setServer("192.168.1.109", 5984);
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLogout() {
		try {
			JSONObject resJson = CouchDB.logout();
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	public void testGetSession() {
		try {
			JSONObject resJson = CouchDB.getSession();
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	public void testCreateDirAndGetFiles() {
		try {
			JSONObject resJson = CouchDB.getFiles(true, true, null);
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
			assertTrue(resJson.has("update_seq"));
			assertTrue(resJson.has("rows"));

			resJson = CouchDB.createDir("test_dir", null,
					new Date().toLocaleString());
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));

			resJson = CouchDB.getFiles(true, true, "test_dir");
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
			assertTrue(resJson.has("update_seq"));
			assertTrue(resJson.has("rows"));
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	public void testCreateFileDocument() {
		try {
			JSONObject resJson = CouchDB.createFileDocument(null);
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
			assertTrue(resJson.has("id"));
			assertTrue(resJson.has("rev"));
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	private String mDefaultPath = Environment.getExternalStorageDirectory()
			.getPath() + "/MediaGrid";

	private void createFile(String filename) {
		File file = new File(mDefaultPath + "/" + filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				fos.write("hello test".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void testUploadAndDownload() {
		// create a file in local storage
		String name = "test_file.txt";
		createFile(name);
		try {
			// first create file document
			JSONObject resJson = CouchDB.createFileDocument(null);
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
			assertTrue(resJson.has("id"));
			assertTrue(resJson.has("rev"));
			String id = resJson.getString("id");
			String rev = resJson.getString("rev");
			// then upload file
			resJson = CouchDB.upload(id, rev, mDefaultPath + "/" + name);
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
			// download file
			assertTrue(CouchDB.doDownloadFile("/media/" + id + "/" + name,
					mDefaultPath + "/" + name));

		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	private String name = new Date().toGMTString().replaceAll("\\s", "").replaceAll(":", "");
	private String pass = GlobalUtil.genRandomPassword();
	private String room = "General";

	/**
	 * this method should run first.
	 */
	public void test0RegisterAndLoginAndGetDocAndSaveUserDoc() {
		try {
			// register first

			JSONObject resJson = CouchDB.register(name, pass, "user");
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
			// then login
			resJson = CouchDB.login(name, pass);
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));

			// then get user document
			resJson = CouchDB.getUserDoc(name);
			assertNotNull(resJson);
			//assertFalse(resJson.has("error"));

			// resJson = CouchDB.saveUserDoc(resJson.getString("_id"),
			// resJson.getString("_rev"),
			// resJson.getString("key"), resJson.getString("type"),
			// resJson.getString("rooms"), userDoc.left);
			// assertNotNull(resJson);
			// assertFalse(resJson.has("error"));

		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	public void testGetChatDBInfo() {
		try {
			JSONObject resJson = CouchDB.getChatDBInfo();
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
			assertTrue(resJson.has("update_seq"));
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	public void testLongPollingChatAndGetMessage() {
		try {
			// long polling chat
			JSONObject resJson = CouchDB.longPollingChat(0, "General");
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));

			// then get messages
			JSONArray results = resJson.getJSONArray("results");
			// get first and last message's ID.
			String lastMsg = results.getJSONObject(results.length() - 1)
					.getString("id");
			String firstMsg = results.getJSONObject(0).getString("id");
			resJson = CouchDB.getMsgs(room, name, firstMsg, lastMsg);
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
			JSONArray rows = resJson.getJSONArray("rows");
			assertNotNull(rows);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	public void testLongPollingIM() {
		// try {
		// JSONObject resJson = CouchDB.longPollingIM(0);
		// assertNotNull(resJson);
		// assertFalse(resJson.has("error"));
		// } catch (Exception e) {
		// e.printStackTrace();
		// assert(false);
		// }
	}

	public void testLongPollingUser() {
		try {
			JSONObject resJson = CouchDB.longPollingUser(0, "General");
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	public void testLongPollingFile() {
		try {
			JSONObject resJson = CouchDB.longPollingFile(0);
			assertNotNull(resJson);
			assertFalse(resJson.has("error"));
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	public void testPostMsg() {
		String msgString = "{'type':'MSG','room':'General','nick':'"
				+ name
				+ "','message':{'jescy':{'msg':'dddd','hmac':''},'jescys':{'msg':'dddd','hmac':''},'Susan':{'msg':'dddd','hmac':''}}}";
		JSONObject msg = null;
		try {
			msg = new JSONObject(msgString);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		JSONObject resJson = CouchDB.postMsg(msg);
		assertNotNull(resJson);
		//assertFalse(resJson.has("error"));
	}

}
