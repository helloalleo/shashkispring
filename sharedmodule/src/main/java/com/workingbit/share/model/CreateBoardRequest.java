package com.workingbit.share.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * CreateBoardRequest
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-09-14T22:16:30.741+03:00")

public class CreateBoardRequest   {
  @JsonProperty("fillBoard")
  private Boolean fillBoard = null;

  @JsonProperty("black")
  private Boolean black = null;

  @JsonProperty("squareSize")
  private Integer squareSize = 1;

  @JsonProperty("rules")
  private EnumRules rules = null;

  public CreateBoardRequest fillBoard(Boolean fillBoard) {
    this.fillBoard = fillBoard;
    return this;
  }

   /**
   * Get fillBoard
   * @return fillBoard
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public Boolean getFillBoard() {
    return fillBoard;
  }

  public void setFillBoard(Boolean fillBoard) {
    this.fillBoard = fillBoard;
  }

  public CreateBoardRequest black(Boolean black) {
    this.black = black;
    return this;
  }

   /**
   * Get black
   * @return black
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public Boolean getBlack() {
    return black;
  }

  public void setBlack(Boolean black) {
    this.black = black;
  }

  public CreateBoardRequest squareSize(Integer squareSize) {
    this.squareSize = squareSize;
    return this;
  }

   /**
   * Get squareSize
   * @return squareSize
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public Integer getSquareSize() {
    return squareSize;
  }

  public void setSquareSize(Integer squareSize) {
    this.squareSize = squareSize;
  }

  public CreateBoardRequest rules(EnumRules rules) {
    this.rules = rules;
    return this;
  }

   /**
   * Get rules
   * @return rules
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public EnumRules getRules() {
    return rules;
  }

  public void setRules(EnumRules rules) {
    this.rules = rules;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateBoardRequest createBoardRequest = (CreateBoardRequest) o;
    return Objects.equals(this.fillBoard, createBoardRequest.fillBoard) &&
        Objects.equals(this.black, createBoardRequest.black) &&
        Objects.equals(this.squareSize, createBoardRequest.squareSize) &&
        Objects.equals(this.rules, createBoardRequest.rules);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fillBoard, black, squareSize, rules);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateBoardRequest {\n");
    
    sb.append("    fillBoard: ").append(toIndentedString(fillBoard)).append("\n");
    sb.append("    black: ").append(toIndentedString(black)).append("\n");
    sb.append("    squareSize: ").append(toIndentedString(squareSize)).append("\n");
    sb.append("    rules: ").append(toIndentedString(rules)).append("\n");
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

