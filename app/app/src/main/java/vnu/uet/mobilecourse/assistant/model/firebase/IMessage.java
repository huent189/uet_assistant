package vnu.uet.mobilecourse.assistant.model.firebase;

public interface IMessage {

    /**
     * Get sender student id
     * such as 17020845, ...
     *
     * @return student id
     */
    String getFromId();

    /**
     * Get sender full name
     * such as 'Nguyen Tung Lam'
     *
     * @return full name
     */
    String getFromName();

    /**
     * @return a array of mentioned participant's student id
     *          such as [17020845, 17020846]
     */
    String[] getMentions();

    /**
     * @return string content of message
     */
    String getContent();

    /**
     * @return send timestamp
     */
    long getTimestamp();

    String getContentType();

    String getAttachmentUri();
}
