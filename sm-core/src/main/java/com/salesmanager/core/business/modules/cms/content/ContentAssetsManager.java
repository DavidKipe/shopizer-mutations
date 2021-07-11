package com.salesmanager.core.business.modules.cms.content;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.modules.cms.common.AssetsManager;
import com.salesmanager.core.business.modules.cms.impl.CMSManager;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.OutputContentFile;

import org.apache.commons.lang3.StringUtils;

public interface ContentAssetsManager extends AssetsManager, FileGet, FilePut, FileRemove, FolderPut, FolderList, FolderRemove, Serializable {
    public static final char UNIX_SEPARATOR = '/';
    public static final char WINDOWS_SEPARATOR = '\\';
    public static String DEFAULT_BUCKET_NAME = "shopizer";
    public static String DEFAULT_REGION_NAME = "us-east-1";
    public static final String ROOT_NAME = "files";

    CMSManager getCmsManager();

    default String bucketName() {
        String name = getCmsManager().getRootName();
								System.out.println("$#68#"); if (StringUtils.isBlank(name)) {
            name = DEFAULT_BUCKET_NAME;
        }
								System.out.println("$#69#"); return name;
    }

    default String nodePath(String store, FileContentType type) {

        StringBuilder builder = new StringBuilder();
        String root = nodePath(store);
        builder.append(root);
								System.out.println("$#70#"); if (type != null && !FileContentType.IMAGE.name().equals(type.name()) && !FileContentType.STATIC_FILE.name().equals(type.name())) {
            builder.append(type.name()).append(Constants.SLASH);
        }

								System.out.println("$#73#"); return builder.toString();

    }

    default String nodePath(String store) {

        StringBuilder builder = new StringBuilder();
        builder.append(ROOT_NAME).append(Constants.SLASH).append(store).append(Constants.SLASH);
								System.out.println("$#74#"); return builder.toString();

    }

    default OutputContentFile getOutputContentFile(byte[] byteArray) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length);
								System.out.println("$#75#"); baos.write(byteArray, 0, byteArray.length);
        OutputContentFile ct = new OutputContentFile();
								System.out.println("$#76#"); ct.setFile(baos);
								System.out.println("$#77#"); return ct;
    }

    default boolean isInsideSubFolder(String key) {
        int c = StringUtils.countMatches(key, Constants.SLASH);
								System.out.println("$#79#"); System.out.println("$#78#"); if (c > 2) {
										System.out.println("$#80#"); return true;
        }
    
								System.out.println("$#81#"); return false;
      }

      default String getName(String filename) {
								System.out.println("$#82#"); if (filename == null) {
										System.out.println("$#83#"); return null;
        }
        int index = indexOfLastSeparator(filename);
								System.out.println("$#85#"); System.out.println("$#84#"); return filename.substring(index + 1);
      }
    
      default int indexOfLastSeparator(String filename) {
								System.out.println("$#86#"); if (filename == null) {
										System.out.println("$#87#"); return -1;
        }
        int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
								System.out.println("$#88#"); return Math.max(lastUnixPos, lastWindowsPos);
      }
    
    

}