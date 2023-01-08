package com.qc.ssm.ssmstudy.reggie.service;

import com.qc.ssm.ssmstudy.reggie.common.R;
import org.springframework.web.multipart.MultipartFile;

public interface CommonService {

    R<String> uploadImage(MultipartFile file);
}
