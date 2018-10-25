package com.viettel.qll.rest;

import java.util.List;
import java.io.File;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.activation.DataHandler;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.viettel.qll.bo.TblPhatRoiMangBO;
import com.viettel.qll.business.TblPhatRoiMangBusinessImpl;
import com.viettel.service.base.dto.DataListDTO;
import com.viettel.qll.dto.TblPhatRoiMangDTO;
import com.viettel.erp.constant.ApplicationConstants;
import com.viettel.ktts2.common.BusinessException;
import com.viettel.ktts2.common.UEncrypt;
import com.viettel.ktts2.common.UFile;
import com.viettel.ktts2.common.UString;
// import com.viettel.service.base.dto.ActionListDTO;
// import com.viettel.erp.utils.ExportExcel;
// import com.viettel.erp.utils.FilterUtilities;
import com.viettel.service.base.utils.DateUtil;
import com.viettel.util.ParamUtils;

/**
 * @author hailh10
 */
 
public class TblPhatRoiMangRsServiceImpl implements TblPhatRoiMangRsService {

	protected final Logger log = Logger.getLogger(TblPhatRoiMangRsService.class);
	@Autowired
	TblPhatRoiMangBusinessImpl tblPhatRoiMangBusinessImpl;
	@Value("${folder_upload2}")
	private String folderUpload;

	@Value("${folder_upload}")
	private String folderTemp;
	@Context HttpServletRequest request;
	@Override
	public Response doSearch(TblPhatRoiMangDTO obj) {
		DataListDTO data = tblPhatRoiMangBusinessImpl.doSearch(obj);
        if (data == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
        	return Response.ok(data).build();
        }
	}
	@Override
	public Response importFile(Attachment attachments, HttpServletRequest request) throws Exception {
		String folderParam = UString.getSafeFileName(request.getParameter("folder"));
		String filePathReturn;
		String filePath;

		DataHandler dataHandler = attachments.getDataHandler();

		// get filename to be uploaded
		MultivaluedMap<String, String> multivaluedMap = attachments.getHeaders();
		String fileName = UFile.getFileName(multivaluedMap);

		// write & upload file to server
		try (InputStream inputStream = dataHandler.getInputStream();) {
			filePath = UFile.writeToFileServerATTT2(inputStream, fileName, folderParam, folderUpload);
			filePathReturn = UEncrypt.encryptFileUploadPath(filePath);
		} catch (Exception ex) {
			throw new BusinessException("Loi khi save file !!!", ex);
		}
	
			
		try {
			
			try {
				String result = tblPhatRoiMangBusinessImpl.importFile(folderUpload + filePath,request);
				if(result.isEmpty()){
					return Response.ok().build();
				}else {
					return Response.ok().entity(Collections.singletonMap("fileName", result)).build();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
				return Response.ok().entity(Collections.singletonMap("error", "Lỗi")).build();
			}

		} catch (IllegalArgumentException e) {
			//return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
			return Response.ok().entity(Collections.singletonMap("error", "Lỗi")).build();
		}
	}
	@Override
	public Response exportExcelGrid(TblPhatRoiMangDTO obj) throws Exception {
		try {
			String result = tblPhatRoiMangBusinessImpl.exportExcelGrid(obj);
			if (result.isEmpty()) {
				return Response.ok().build();
			} else {
				return Response.ok().entity(Collections.singletonMap("fileName", result)).build();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
		}
	}
	@Override
	public Response downloadImportTemplate() throws Exception {
		// TODO Auto-generated method stub
				try {
					String result = tblPhatRoiMangBusinessImpl.downloadImportTemplate();
					if (result.isEmpty()) {
						return Response.ok().build();
					} else {
						return Response.ok().entity(Collections.singletonMap("fileName", result)).build();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
				}
			}
	}

