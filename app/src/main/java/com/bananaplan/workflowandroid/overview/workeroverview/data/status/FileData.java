package com.bananaplan.workflowandroid.overview.workeroverview.data.status;

import android.net.Uri;

/**
 * Created by Ben on 2015/8/29.
 */
public class FileData extends BaseData {
    public long uploader;
    public String fileName;
    public Uri filePath;

    public FileData(BaseData.TYPE type) {
        super(type);
    }
}
