package helpers;

import java.util.HashMap;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ShaderFactory {
  public static final String TAG = ShaderFactory.class.getSimpleName();
  
  private static HashMap<InstanceMapKey, UsageCountedShaderProgram> programInstances = 
      new HashMap<InstanceMapKey, UsageCountedShaderProgram>();
  private static VersatileMapKey versatileMapKey = new VersatileMapKey();
  
  private static String removePrecisionQualifier(String shaderCode) {
    if (shaderCode.trim().startsWith("precision")) {
      return shaderCode.substring(shaderCode.indexOf("\n") + "\n".length());
    }
    return shaderCode;
  }
  
  private static boolean hasPrecisionQualifier(String shaderCode) {
    return shaderCode.trim().startsWith("precision");
  }
  
  private static UsageCountedShaderProgram createShaderProgram_GL(String vertexShader, String fragmentShader) {
    return new UsageCountedShaderProgram(
        removePrecisionQualifier(vertexShader), removePrecisionQualifier(fragmentShader));
  }
  
  private static UsageCountedShaderProgram createShaderProgram_GL_ES(String vertexShader, String fragmentShader) {
    UsageCountedShaderProgram program = new UsageCountedShaderProgram(vertexShader, fragmentShader);
    if (!program.isCompiled()) {
      program.dispose();
      if (!hasPrecisionQualifier(vertexShader)) {
        Gdx.app.log(TAG, "Shader is missing precision qualifier:\n" + vertexShader);
        vertexShader = "precision mediump float;\n" + vertexShader;
      }
      if (!hasPrecisionQualifier(fragmentShader)) {
        Gdx.app.log(TAG, "Shader is missing precision qualifier:\n" + fragmentShader);
        fragmentShader = "precision lowp float;\n" + fragmentShader;
      }
      program = new UsageCountedShaderProgram(vertexShader, fragmentShader);
    }
    return program;
  }
  
  public static ShaderProgram createShaderProgram(String vertexShader, String fragmentShader) {
    versatileMapKey.setShaders(vertexShader, fragmentShader);
    UsageCountedShaderProgram program = programInstances.get(versatileMapKey);
    if (program == null) {
      String errorLog = null;
      if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.HeadlessDesktop) {
        program = createShaderProgram_GL(vertexShader, fragmentShader);
        if (!program.isCompiled()) {
          errorLog = program.getLog();
          Gdx.app.log(TAG, "Compilation as GL shader failed - attempting to use GL ES");
          program = createShaderProgram_GL_ES(vertexShader, fragmentShader);
        }
      } else {
        program = createShaderProgram_GL_ES(vertexShader, fragmentShader);
        if (!program.isCompiled()) {
          errorLog = program.getLog();
          Gdx.app.log(TAG, "Compilation as GL ES shader failed - attempting to use GL");
          program = createShaderProgram_GL(vertexShader, fragmentShader);
        }
      }
      if (errorLog != null)
        throw new GdxRuntimeException("Error while compiling shader: " + errorLog);
    }
    program.incUsageCounter();
    return program;
  }
  
  private interface InstanceMapKey {
    public String getVertexShaderSource();
    
    public String getFragmentShaderSource();
  }
  
  private static class VersatileMapKey implements InstanceMapKey {
    private String vertexShader, fragmentShader;
    
    public void setShaders(String vertexShader, String fragmentShader) {
      this.vertexShader = vertexShader;
      this.fragmentShader = fragmentShader;
    }
    
    @Override
    public String getVertexShaderSource() {
      return vertexShader;
    }

    @Override
    public String getFragmentShaderSource() {
      return fragmentShader;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + vertexShader.length();
      result = prime * result + fragmentShader.length();
      return result;
    }

    @Override
    public boolean equals(Object object) {
      if (this == object)
        return true;
      if (object == null)
        return false;
      if (object instanceof InstanceMapKey) {
        InstanceMapKey other = (InstanceMapKey)object;
        return vertexShader.equals(other.getVertexShaderSource()) && 
            fragmentShader.equals(other.getFragmentShaderSource());
      }
      return false;
    }
  }
  
  private static class UsageCountedShaderProgram extends ShaderProgram implements InstanceMapKey {
    private int usageCounter = 0;
    
    public UsageCountedShaderProgram(FileHandle vertexShader, FileHandle fragmentShader) {
      super(vertexShader, fragmentShader);
      programInstances.put(this, this);
    }

    public UsageCountedShaderProgram(String vertexShader, String fragmentShader) {
      super(vertexShader, fragmentShader);
      programInstances.put(this, this);
    }
    
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getVertexShaderSource().length();
      result = prime * result + getFragmentShaderSource().length();
      return result;
    }

    @Override
    public boolean equals(Object object) {
      if (this == object)
        return true;
      if (object == null)
        return false;
      if (object instanceof InstanceMapKey) {
        InstanceMapKey other = (InstanceMapKey)object;
        return getVertexShaderSource().equals(other.getVertexShaderSource()) && 
            getFragmentShaderSource().equals(other.getFragmentShaderSource());
      }
      return false;
    }

    public void incUsageCounter() {
      usageCounter++;
    }

    @Override
    public void dispose() {
      usageCounter--;
      if (usageCounter <= 0) {
        programInstances.remove(this);
        super.dispose();
      }
    }
  }
}
