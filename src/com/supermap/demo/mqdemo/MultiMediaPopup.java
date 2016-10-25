package com.supermap.demo.mqdemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.supermap.data.DatasetVector;
import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.imb.jsonlib.SiJsonObject;
import com.supermap.mdatacollector.MDataCollector;
import com.supermap.mdatacollector.NetworkAccessMediaFileListener;
import com.supermap.plugin.LocationManagePlugin.GPSData;
import com.supermap.services.DataDownloadService;
import com.supermap.services.DataUploadService;
import com.supermap.services.FeatureSet;
import com.supermap.services.ResponseCallback;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.AdapterView.OnItemClickListener;

public class MultiMediaPopup extends PopupWindow implements OnClickListener {
    private LayoutInflater m_LayoutInflater = null;
    private View m_ContentView = null;
    private View m_mainView = null;

    private MainActivity m_MainActivity = null;

    // private ListView m_lvMultiMediaTypeList;

    private MDataCollector m_MDataCollector = null;
    private DataDownloadService m_DownloadService = null;
    private DataUploadService m_UploadService = null;

    private Workspace m_Workspace = null;
    private AudioRecordPopup m_popupAudioRecord = null;

    private Point2D m_GPSPoint = new Point2D();

	// ��Դ�ϴ�ʱ����ʹ�������δ��ֵ��
	// ��������������Ҫ�Լ���ֵ���ֱ����Լ������iportal�������ַ���û��������루����ԱȨ�ޣ�
	private String m_IPortalURI;
    private String m_IPortalUserName;
    private String m_IPortalPassword;

    private String portalInfo = "{content_type=1}" + "{uri=" + m_IPortalURI + "," + "username=" + m_IPortalUserName + "," + "passwd=" + m_IPortalPassword + "}";

    private String dvName = "MQDemo_MediaDataset"; // �洢��ý���ļ���Ϣ�ĵ����ݼ�������
    private String serviceName = "data-mqdemo"; // iServer�еķ�����
    private String datasourceName = "multimedia"; // iServer�Ϸ���������Դ��
    private String urlDataset; // iServer��Dataset��������ַ

    public Rectangle2D m_Rect = new Rectangle2D(116, 39, 117, 40);

    private boolean m_bLogin = false; // �Ƿ��Ѿ���½
    private NetworkAccessMediaFileListener m_NetworkListener = new NetworkAccessMediaFileListener() {

        @Override
        public boolean uploadMediaFile(String mediaFileName) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onUploadMediaFiles(String uploadInfo) {
            String msg = portalInfo;
            // msg += serverInfo;
            String rect = m_Rect.toJson();
            msg += rect;
            m_MainActivity.m_MessageQueue.sendMessageByType(msg, 1);
        }

        @Override
        public void onDownloadMediaFiles(String downloadInfo) {
            String str = downloadInfo;
            System.out.println(str);
        }

        @Override
        public boolean downloadMediaFile(String mediaFileName) {
            return false;
        }
    };

    private ResponseCallback serviceCallback = new ResponseCallback() {

        @Override
        public void dataServiceFinished(String arg0) {
            Runnable run = new Runnable() {

                @Override
                public void run() {
                    try {
                        m_MDataCollector.downloadMediaFiles(m_Rect);

                        // m_MDataCollector.login(m_IPortalURI,
                        // m_IPortalUserName, m_IPortalPassword);
                        // Rectangle2D rect = new Rectangle2D(116, 39, 117, 40);
                        m_MDataCollector.uploadMediaFiles(m_Rect);
                    } catch (NullPointerException ex) {

                    }
                }
            };
            new Thread(run).start();
        }

        @Override
        public void receiveResponse(FeatureSet arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void requestFailed(String arg0) {
            String str_uploadInfo = "�ϴ����ݼ�ʧ�� :\n" + arg0;
            System.out.println(str_uploadInfo);
        }

        @Override
        public void requestSuccess() {
            String str = "test";
        }

    };

    public MultiMediaPopup(View mainView, Context context, MainActivity mainActivity) {
        super(mainActivity);

        m_LayoutInflater = LayoutInflater.from(context);
        m_mainView = mainView;
        m_MainActivity = mainActivity;

        m_MDataCollector = new MDataCollector(mainActivity);
        m_MDataCollector.addNetworkAccessMediaFileListener(m_NetworkListener);

        m_DownloadService = new DataDownloadService(m_IPortalURI);
        m_UploadService = new DataUploadService(m_IPortalURI);

        m_DownloadService.setResponseCallback(serviceCallback);
        m_UploadService.setResponseCallback(serviceCallback);

        urlDataset = m_IPortalURI + "/services/" + serviceName + "/rest/data/datasources/" + datasourceName + "/datasets/" + dvName;
        m_DownloadService.setUrl(m_IPortalURI);
        m_UploadService.setUrl(m_IPortalURI);

        initView();

        m_popupAudioRecord = new AudioRecordPopup(mainView, context, mainActivity);
        m_popupAudioRecord.setParent(this);

        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    private void dismissPopupWindow() {
        // m_parentView.setBackgroundColor(Color.rgb(241, 237, 237));
        this.dismiss();
    }

    private void initView() {
        m_ContentView = m_LayoutInflater.inflate(R.layout.multi_media_list, null);
        setContentView(m_ContentView);

        ((Button) m_ContentView.findViewById(R.id.btn_image)).setOnClickListener(this);
        ((Button) m_ContentView.findViewById(R.id.btn_vedio)).setOnClickListener(this);
        ((Button) m_ContentView.findViewById(R.id.btn_audio)).setOnClickListener(this);

    }

    public void show(View parent) {
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // m_parentView = parent;

        DisplayMetrics dm = m_mainView.getContext().getResources().getDisplayMetrics();
        // ��ȡϵͳ״̬��
        Rect outRect = new Rect();
        m_MainActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);

        showAtLocation(m_mainView, Gravity.LEFT | Gravity.TOP, 8, (int) ((120 * dm.density) / 2) + 10 + outRect.top);
    }

    public void setWorkspace(Workspace workspace, Rectangle2D rect) {
        m_Workspace = workspace;
        if (m_Workspace != null) {
            m_MDataCollector.setMediaDataset(m_Workspace.getDatasources().get(1), "MQDemo_MediaDataset");

            m_Rect = rect;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    if (!m_bLogin) {
                    	try {
	                       m_MDataCollector.login(m_IPortalURI, m_IPortalUserName, m_IPortalPassword);
	                        m_bLogin = true;
                    	} catch (Exception ex) {
                    		ex.printStackTrace();
                    	}
 
                    }
                    // �������ݼ�
                    if (m_DownloadService != null) {
                        m_DownloadService.updateDataset(urlDataset, (DatasetVector) m_Workspace.getDatasources().get(1).getDatasets().get(dvName));
                    }

                    m_MDataCollector.downloadMediaFiles(m_Rect);
                }
            }).start();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        m_MDataCollector.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {

            Runnable run = new Runnable() {

                @Override
                public void run() {
                    // �ϴ���ý������
                    if (!m_bLogin) {
                    	try {
 	                       m_MDataCollector.login(m_IPortalURI, m_IPortalUserName, m_IPortalPassword);
 	                        m_bLogin = true;
                     	} catch (Exception ex) {
                     		ex.printStackTrace();
                     	}
                    }
                    // // ȷ��
                    // m_DownloadService.updateDataset(urlDataset,(DatasetVector)
                    // m_Workspace.getDatasources().get(1).getDatasets().get(dvName));

                    // �ϴ�����
                    m_UploadService.commitDataset(urlDataset, (DatasetVector) m_MDataCollector.getMediaDataset());

                }
            };
            new Thread(run).start();
        } else {
            // ȡ��
        }
    }

    public void setGPSData(GPSData gpsData) {
        m_MDataCollector.setGPSData(gpsData);
        m_GPSPoint.setX(gpsData.dLatitude);
        m_GPSPoint.setY(gpsData.dLongitude);
    }

    public void processReceivedInfo(String msg) {
        // {uri=http://support.supermap.com.cn:8092/iportal,username=supermap,passwd=bdpc123}{
        // "leftBottom" :{ "x" : 116.0, "y" : 39.0}, "rightTop" : { "x" : 117.0,
        // "y" : 40.0}}

        String rect;
        String loginfo;
        int pos = msg.indexOf("}");
        loginfo = msg.substring(0, pos + 1);
        rect = msg.substring(pos + 1, msg.length());

        // {
        loginfo = loginfo.substring(1, loginfo.length() - 1);

        // uri=http://support.supermap.com.cn:8092/iportal,username=supermap,passwd=bdpc123
        pos = loginfo.indexOf(",");
        String uriTmp = loginfo.substring(0, pos);
        loginfo = loginfo.substring(pos + 1, loginfo.length());
        pos = uriTmp.indexOf("=");
        final String uri = uriTmp.substring(pos + 1);

        // username=supermap,passwd=bdpc123
        pos = loginfo.indexOf(",");
        String userTmp = loginfo.substring(0, pos);
        String pwdTmp = loginfo.substring(pos + 1);
        pos = userTmp.indexOf("=");
        final String user = userTmp.substring(pos + 1);

        // passwd=bdpc123
        pos = pwdTmp.indexOf("=");
        final String pwd = pwdTmp.substring(pos + 1);
        // }

        {
            // { "leftBottom" :{ "x" : 116.0, "y" : 39.0}, "rightTop" : { "x" :
            // 117.0, "y" : 40.0}}
            // Rectangle2D rectangle = new Rectangle2D();
            // rectangle = fromJsonRect(new SiJsonObject(rect));
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!m_bLogin) {
                    // m_MDataCollector.login(m_IPortalURI, m_IPortalUserName,
                    // m_IPortalPassword);
                    
                	try {
                		m_MDataCollector.login(uri, user, pwd);
	                        m_bLogin = true;
                 	} catch (Exception ex) {
                 		ex.printStackTrace();
                 	}


                }
                m_DownloadService.updateDataset(urlDataset, (DatasetVector) m_Workspace.getDatasources().get(1).getDatasets().get(dvName));
                m_MDataCollector.downloadMediaFiles(m_Rect);
            }
        }).start();

    }

    public Rectangle2D fromJsonRect(SiJsonObject object) {
        Rectangle2D rect = new Rectangle2D();
        String leftBottom = object.getString("leftBottom");
        String rightTop = object.getString("rightTop");
        rect.setLeft(new SiJsonObject(leftBottom).getDouble("x"));
        rect.setBottom(new SiJsonObject(leftBottom).getDouble("y"));
        rect.setRight(new SiJsonObject(rightTop).getDouble("x"));
        rect.setTop(new SiJsonObject(rightTop).getDouble("y"));
        return rect;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_image: {
            m_MDataCollector.captureImage();
            dismissPopupWindow();
        }
            break;
        case R.id.btn_vedio: {
            m_MDataCollector.captureVideo();
            dismissPopupWindow();
        }
            break;
        case R.id.btn_audio: {
            m_popupAudioRecord.setMDataCollector(m_MDataCollector);
            m_popupAudioRecord.show(v);
            dismissPopupWindow();
        }
            break;
        default:
            break;
        }
        this.dismiss();
    }

    public void sendMultiMediaFiles() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!m_bLogin) {
                    
                    try {
                    	m_MDataCollector.login(m_IPortalURI, m_IPortalUserName, m_IPortalPassword);
	                    m_bLogin = true;
                 	} catch (Exception ex) {
                 		ex.printStackTrace();
                 	}
                }
                m_UploadService.commitDataset(urlDataset, (DatasetVector) m_Workspace.getDatasources().get(1).getDatasets().get(0));
                // m_MDataCollector.uploadMediaFiles(m_Rect);

            }
        }).start();
    }

    public void clearMultiMedia() {
    	// ������ض�ý������
    	m_MDataCollector.removeMediaFilesWithBounds(m_Rect);
    	// ���server�����ݷ����ϵ�����
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String featuresUrl = m_IPortalURI + "/services/" + serviceName + "/rest/data/datasources/" + datasourceName + "/datasets/" + dvName
                            + "/features.json";
                    String featureListStr = request(featuresUrl, "GET", null);
                    JSONObject resultAllJson = new JSONObject(featureListStr);
                    String strResult1 = resultAllJson.getString("childUriList");
                    strResult1 = strResult1.replaceAll("\\\\/", "/");
                    JSONArray resultIdArray = new JSONArray(strResult1);
                    for (int i = 0; i < resultIdArray.length(); i++) {
                        String strid = resultIdArray.getString(i);
                        request(strid + ".json", "DELETE", null);
                    }
                } catch (JSONException e) {
                    System.out.println("JSONException" + e.getMessage());
                } catch (RuntimeException e) {
                    System.out.println("RuntimeException" + e.getMessage());
                }
                // String generateTokenPara = "{\"userName\": \"user1\",\"password\": \"secret\",\"clientType\": \"RequestIP\",\"expiration\": 60}";
                String generateTokenPara = "{\"userName\": \"" + m_IPortalUserName + "\",\"password\": \"" + m_IPortalPassword
                        + "\",\"clientType\": \"RequestIP\",\"expiration\": 60}";
                String token = request(m_IPortalURI + "/services/security/tokens.json", "POST", generateTokenPara);
                try {
                    while (true) {
                        String mycontentDataUrl = m_IPortalURI + "/web/mycontent/datas.json?pageSize=20&token=" + token;
                        String deleteContentDataUrl = m_IPortalURI + "/web/mycontent/datas/delete.json?token=" + token;
                        String contentDataStr = request(mycontentDataUrl, "GET", null);
                        JSONObject resultAllJson = new JSONObject(contentDataStr);
                        int total = resultAllJson.getInt("total");
                        if (total < 1) {
                            break;
                        }
                        String strResult = resultAllJson.getString("content");
                        StringBuilder entityStringB = new StringBuilder();
                        entityStringB.append("[");
                        JSONArray resultIdArray = new JSONArray(strResult);
                        for (int i = 0; i < resultIdArray.length() - 1; i++) {
                            String strid = resultIdArray.getJSONObject(i).getString("id");
                            entityStringB.append(strid);
                            entityStringB.append(",");
                        }
                        entityStringB.append(resultIdArray.getJSONObject(resultIdArray.length() - 1).getString("id"));
                        entityStringB.append("]");
                        request(deleteContentDataUrl, "POST", entityStringB.toString());
                    }
                } catch (JSONException e) {
                    System.out.println("JSONException" + e.getMessage());
                } catch (RuntimeException e) {
                    System.out.println("RuntimeException" + e.getMessage());
                }
            }
        }).start();
    }

    private static String request(String url, String method, String entity) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod(method);
            if (entity != null && entity.trim().length() > 0 && ("put".equalsIgnoreCase(method) || "post".equalsIgnoreCase(method))) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream os = conn.getOutputStream();
                try {
                    IOUtils.write(entity, os, Charset.forName("utf-8"));
                } finally {
                    IOUtils.closeQuietly(os);
                }
            }
            int responseCode = conn.getResponseCode();
            InputStream is = responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream();
            try {
                return IOUtils.toString(is, Charset.forName("utf-8"));
            } finally {
                IOUtils.closeQuietly(is);
                conn.disconnect();
            }
        } catch (MalformedURLException e) {
            throw new IllegalStateException("url_malformed");
        } catch (IOException e) {
            throw new IllegalStateException("request_occursioexception");
        }
    }

    public class GroupAdapter extends BaseAdapter {
        private Context m_context;
        private List<String> m_listMultiMedia;

        public GroupAdapter(Context context) {
            this.m_context = context;
            m_listMultiMedia = new LinkedList<String>();
            m_listMultiMedia.add("��Ƭ");
            m_listMultiMedia.add("��Ƶ");
            m_listMultiMedia.add("��Ƶ");
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return m_listMultiMedia.size();
        }

        @Override
        public Object getItem(int position) {
            if (m_listMultiMedia.size() == 0) {
                return null;
            } else {
                return m_listMultiMedia.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            ViewHolder viewHolder = null;

            if (convertView == null) {
                convertView = m_LayoutInflater.inflate(R.layout.multi_media_item, null);
                viewHolder = new ViewHolder();

                convertView.setTag(viewHolder);
                viewHolder.textMediaType = (TextView) convertView.findViewById(R.id.multi_media_item);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textMediaType.setText(this.m_listMultiMedia.get(position));
            return convertView;
        }

    }

    static class ViewHolder {
        TextView textMediaType;
    }

}
