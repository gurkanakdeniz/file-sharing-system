package com.share.fileupload.model;

import com.share.fileupload.enm.ResultStatus;

public class ResultModel extends BaseModel {

    private ErrorModel error;

    private ResultStatus result;

    public ResultModel() {
        this.error  = null;
        this.result = ResultStatus.SUCCESS;
    }

    public ResultModel(ErrorModel error) {
        this.error  = error;
        this.result = ResultStatus.ERROR;
    }

    public ResultModel(Exception ex) {
        this.error  = new ErrorModel(ex);
        this.result = ResultStatus.ERROR;
    }

    public ResultModel(ResultStatus status) {
        this.error  = null;
        this.result = status;
    }

    public ResultModel(ErrorModel error, ResultStatus status) {
        this.error  = error;
        this.result = status;
    }

    public ResultModel(Exception ex, ResultStatus status) {
        this.error  = new ErrorModel(ex);
        this.result = status;
    }

    public ResultModel(Exception ex, String description) {
        this.error  = new ErrorModel(ex, description);
        this.result = ResultStatus.ERROR;
    }

    public ResultModel(Exception ex, String description, ResultStatus status) {
        this.error  = new ErrorModel(ex, description);
        this.result = status;
    }

    public ErrorModel getError() {
        return error;
    }

    public void setError(ErrorModel error) {
        this.error = error;
        setResult(ResultStatus.ERROR);
    }
      
    public void setError(Exception ex) {
        this.error  = new ErrorModel(ex);
        setResult(ResultStatus.ERROR);
    }

    public ResultStatus getResult() {
        return result;
    }

    public void setResult(ResultStatus result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return result == ResultStatus.SUCCESS;
    }
}