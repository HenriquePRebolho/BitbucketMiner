
package aiss.BitbucketMiner.BitbucketMiner.model;


import aiss.BitbucketMiner.BitbucketMiner.model.issue.Links__2;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Project {

    @JsonProperty("type")
    private String type;
    @JsonProperty("owner")
    private aiss.BitbucketMiner.BitbucketMiner.model.Owner owner;
    @JsonProperty("workspace")
    private aiss.BitbucketMiner.BitbucketMiner.model.Workspace workspace;
    @JsonProperty("key")
    private String key;
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("is_private")
    private Boolean isPrivate;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("links")
    private Links__2 links;
    @JsonProperty("created_on")
    private String createdOn;
    @JsonProperty("updated_on")
    private String updatedOn;
    @JsonProperty("has_publicly_visible_repos")
    private Boolean hasPubliclyVisibleRepos;

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("owner")
    public aiss.BitbucketMiner.BitbucketMiner.model.Owner getOwner() {
        return owner;
    }

    @JsonProperty("owner")
    public void setOwner(aiss.BitbucketMiner.BitbucketMiner.model.Owner owner) {
        this.owner = owner;
    }

    @JsonProperty("workspace")
    public aiss.BitbucketMiner.BitbucketMiner.model.Workspace getWorkspace() {
        return workspace;
    }

    @JsonProperty("workspace")
    public void setWorkspace(aiss.BitbucketMiner.BitbucketMiner.model.Workspace workspace) {
        this.workspace = workspace;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("is_private")
    public Boolean getIsPrivate() {
        return isPrivate;
    }

    @JsonProperty("is_private")
    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("links")
    public Links__2 getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(Links__2 links) {
        this.links = links;
    }

    @JsonProperty("created_on")
    public String getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("created_on")
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    @JsonProperty("updated_on")
    public String getUpdatedOn() {
        return updatedOn;
    }

    @JsonProperty("updated_on")
    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    @JsonProperty("has_publicly_visible_repos")
    public Boolean getHasPubliclyVisibleRepos() {
        return hasPubliclyVisibleRepos;
    }

    @JsonProperty("has_publicly_visible_repos")
    public void setHasPubliclyVisibleRepos(Boolean hasPubliclyVisibleRepos) {
        this.hasPubliclyVisibleRepos = hasPubliclyVisibleRepos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Project.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null)?"<null>":this.type));
        sb.append(',');
        sb.append("owner");
        sb.append('=');
        sb.append(((this.owner == null)?"<null>":this.owner));
        sb.append(',');
        sb.append("workspace");
        sb.append('=');
        sb.append(((this.workspace == null)?"<null>":this.workspace));
        sb.append(',');
        sb.append("key");
        sb.append('=');
        sb.append(((this.key == null)?"<null>":this.key));
        sb.append(',');
        sb.append("uuid");
        sb.append('=');
        sb.append(((this.uuid == null)?"<null>":this.uuid));
        sb.append(',');
        sb.append("isPrivate");
        sb.append('=');
        sb.append(((this.isPrivate == null)?"<null>":this.isPrivate));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("links");
        sb.append('=');
        sb.append(((this.links == null)?"<null>":this.links));
        sb.append(',');
        sb.append("createdOn");
        sb.append('=');
        sb.append(((this.createdOn == null)?"<null>":this.createdOn));
        sb.append(',');
        sb.append("updatedOn");
        sb.append('=');
        sb.append(((this.updatedOn == null)?"<null>":this.updatedOn));
        sb.append(',');
        sb.append("hasPubliclyVisibleRepos");
        sb.append('=');
        sb.append(((this.hasPubliclyVisibleRepos == null)?"<null>":this.hasPubliclyVisibleRepos));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
