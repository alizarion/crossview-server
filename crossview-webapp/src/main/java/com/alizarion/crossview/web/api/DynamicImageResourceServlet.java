package com.alizarion.crossview.web.api;

import com.alizarion.crossview.mbean.CrossViewConfigMBean;
import com.alizarion.crossview.services.EntityFacade;
import com.alizarion.reference.exception.ApplicationException;
import com.alizarion.reference.filemanagement.entities.ImageManagedFile;
import com.alizarion.reference.filemanagement.entities.ManagedImageScaledCacheVisitor;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet that provide resized images, request path MUST
 * be like id[ManagedFile.UUID]_w[Width]_h[Height].jpg
 * width and height are optional, if not return the image initial size.
 * @author selim@openlinux.fr.
 */
@WebServlet(urlPatterns = "/images/dynamic/*")
public class DynamicImageResourceServlet extends HttpServlet {

    private static final long serialVersionUID = 2714837846601535223L;

    @EJB
    EntityFacade facade;

    @EJB
    private CrossViewConfigMBean config;



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String,String> params =  extractParams(request);
        if (params  == null){
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            ImageManagedFile image = facade.getImageByUUID(params.get("id"));
            if (image == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                try {
                    ManagedImageScaledCacheVisitor scaledCacheVisitor =
                            new ManagedImageScaledCacheVisitor(
                                    config.getManagedFileRootFolder().toString(),
                                    params.get("width") != null ?
                                            Integer.parseInt(params.get("width")) : null,
                                    params.get("height") != null ?
                                            Integer.parseInt(params.get("height")):null);
                    scaledCacheVisitor.visit(image);
                    BufferedInputStream in = new BufferedInputStream(
                            new FileInputStream(scaledCacheVisitor.getCacheFile()));

                    // Get image contents.
                    byte[] bytes = new byte[in.available()];
                    in.read(bytes);
                    in.close();
                    // Write image contents to response.
                    response.getOutputStream().write(bytes);
                } catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private Map<String,String> extractParams(HttpServletRequest request){
        if (!StringUtils.isEmpty(request.getPathInfo())) {
            String img = request.getPathInfo().substring(1, request.getPathInfo().length());
            if(0 < img.lastIndexOf('.')) {
                img = img.substring(0, img.lastIndexOf('.'));
            }
            String[] params = img.split("_");
            Map<String, String> extracted = new HashMap<>();

            for (String param : params){
                switch (param.charAt(0)) {
                    case 'i': extracted.put("id",param.substring(2,param.length()));
                        break;
                    case 'w' : extracted.put("width",param.substring(1,param.length()));
                        break;
                    case 'h' : extracted.put("height",param.substring(1,param.length()));
                        break;
                    default: break;

                }

            }

            return extracted;
        }  else {
            return null;
        }
    }


}
