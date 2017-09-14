package com.workingbit.board.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * SquareList
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-09-14T22:31:52.798+03:00")

public class SquareList   {
  @JsonProperty("allowed")
  private List<com.workingbit.share.domain.impl.Square> allowed = null;

  @JsonProperty("beaten")
  private List<com.workingbit.share.domain.impl.Square> beaten = null;

  public SquareList allowed(List<com.workingbit.share.domain.impl.Square> allowed) {
    this.allowed = allowed;
    return this;
  }

  public SquareList addAllowedItem(com.workingbit.share.domain.impl.Square allowedItem) {
    if (this.allowed == null) {
      this.allowed = new ArrayList<com.workingbit.share.domain.impl.Square>();
    }
    this.allowed.add(allowedItem);
    return this;
  }

   /**
   * Get allowed
   * @return allowed
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<com.workingbit.share.domain.impl.Square> getAllowed() {
    return allowed;
  }

  public void setAllowed(List<com.workingbit.share.domain.impl.Square> allowed) {
    this.allowed = allowed;
  }

  public SquareList beaten(List<com.workingbit.share.domain.impl.Square> beaten) {
    this.beaten = beaten;
    return this;
  }

  public SquareList addBeatenItem(com.workingbit.share.domain.impl.Square beatenItem) {
    if (this.beaten == null) {
      this.beaten = new ArrayList<com.workingbit.share.domain.impl.Square>();
    }
    this.beaten.add(beatenItem);
    return this;
  }

   /**
   * Get beaten
   * @return beaten
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<com.workingbit.share.domain.impl.Square> getBeaten() {
    return beaten;
  }

  public void setBeaten(List<com.workingbit.share.domain.impl.Square> beaten) {
    this.beaten = beaten;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SquareList squareList = (SquareList) o;
    return Objects.equals(this.allowed, squareList.allowed) &&
        Objects.equals(this.beaten, squareList.beaten);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowed, beaten);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SquareList {\n");
    
    sb.append("    allowed: ").append(toIndentedString(allowed)).append("\n");
    sb.append("    beaten: ").append(toIndentedString(beaten)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

