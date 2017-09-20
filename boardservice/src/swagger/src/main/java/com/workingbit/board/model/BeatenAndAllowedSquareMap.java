package com.workingbit.board.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.workingbit.board.model.MovesList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * a (key, BeatenAndAllowedItem) map. &#x60;default&#x60; is an example key
 */
@ApiModel(description = "a (key, BeatenAndAllowedItem) map. `default` is an example key")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-09-19T23:30:23.706+03:00")

public class BeatenAndAllowedSquareMap extends HashMap<String, List>  {
  @JsonProperty("default")
  private MovesList _default = null;

  public BeatenAndAllowedSquareMap _default(MovesList _default) {
    this._default = _default;
    return this;
  }

   /**
   * Get _default
   * @return _default
  **/
  @ApiModelProperty(value = "")

  @Valid

  public MovesList getDefault() {
    return _default;
  }

  public void setDefault(MovesList _default) {
    this._default = _default;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeatenAndAllowedSquareMap beatenAndAllowedSquareMap = (BeatenAndAllowedSquareMap) o;
    return Objects.equals(this._default, beatenAndAllowedSquareMap._default) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_default, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeatenAndAllowedSquareMap {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    _default: ").append(toIndentedString(_default)).append("\n");
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

