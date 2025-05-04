
package aiss.BitbucketMiner.BitbucketMiner.model;

import aiss.BitbucketMiner.BitbucketMiner.model.issue.Self__2;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Links__2 {

    @JsonProperty("self")
    private Self__2 self;

    @JsonProperty("self")
    public Self__2 getSelf() {
        return self;
    }

    @JsonProperty("self")
    public void setSelf(Self__2 self) {
        this.self = self;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Links__2 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("self");
        sb.append('=');
        sb.append(((this.self == null)?"<null>":this.self));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
