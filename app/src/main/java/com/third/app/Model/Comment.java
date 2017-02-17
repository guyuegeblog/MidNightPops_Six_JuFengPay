package com.third.app.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Author: Jan
 * CreateTime:on 2016/10/27.
 */
public class Comment implements Serializable {
    private List<CommentInfo> commentJson;

    public List<CommentInfo> getCommentJson() {
        return commentJson;
    }

    public void setCommentJson(List<CommentInfo> commentJson) {
        this.commentJson = commentJson;
    }
}
