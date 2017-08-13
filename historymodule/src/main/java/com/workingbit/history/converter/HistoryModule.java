package com.workingbit.history.converter;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.rutledgepaulv.prune.Tree;

/**
 * Created by Aleksey Popryaduhin on 21:47 13/08/2017.
 */
public class HistoryModule extends SimpleModule{
  public HistoryModule() {
    super("MyModule", new Version(0,0,1,"", "com.workingbit.history", "historymodule"));
  }

  @Override
  public void setupModule(SetupContext context) {
    context.setMixInAnnotations(Tree.Node.class, MixIn.class);
//    context.appendAnnotationIntrospector(new IgnoranceIntrospector());
  }
}
