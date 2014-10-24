package json;

/**
 * Created by anthony on 8/12/14.
 */
public class PrivateEventRequest extends JsonData {
  public Long pk;

  public static PrivateEventRequest fromJson(String json) {
    {
      return JsonUtils.toObject(json, PrivateEventRequest.class);
    }
  }
}
