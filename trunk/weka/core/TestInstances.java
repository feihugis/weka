/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 * TestInstances.java
 * Copyright (C) 2006 University of Waikato, Hamilton, New Zealand
 */

package weka.core;

import weka.core.Capabilities.Capability;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

/**
 * Generates artificial datasets for testing. In case of Multi-Instance data
 * the settings for the number of attributes applies to the data inside
 * the bag. Originally based on code from the CheckClassifier.<p/>
 * 
 * Valid options are: <p/>
 * 
 * -h|-help <br/>
 *  prints this help <p/>
 *  
 * -relation name <br/>
 *  The name of the data set. <p/>
 *  
 * -seed num <br/>
 *  The seed value. <p/>
 *  
 * -num-instances num <br/>
 *  The number of instances in the datasets (default 20). <p/>
 *  
 * -class-type num <br/>
 *  The class type, see constants in weka.core.Attribute
 *  (default 1=nominal). <p/>
 *  
 * -classes-values num <br/>
 *  The number of classes to generate (for nominal classes only)
 *  (default 2). <p/>
 *  
 * -class-index num <br/>
 *  The class index, with -1=last, (default -1). <p/>
 *  
 * -no-class <br/>
 *  Doesn't include a class attribute in the output. <p/>
 *  
 * -nominal num <br/>
 *  The number of nominal attributes (default 1). <p/>
 *  
 * -nominal-values num <br/>
 *  The number of values for nominal attributes (default 2). <p/>
 *  
 * -numeric num <br/>
 *  The number of numeric attributes (default 0). <p/>
 *  
 * -string num <br/>
 *  The number of string attributes (default 0). <p/>
 *  
 * -date num <br/>
 *  The number of date attributes (default 0). <p/>
 *  
 * -relational num <br/>
 *  The number of relational attributes (default 0). <p/>
 *  
 * -multi-instance <br/>
 *  Generates multi-instance data. <p/>
 *  
 * -W classname <br/>
 *  The Capabilities handler to get base the dataset on.
 *  The other parameters can be used to override the ones
 *  determined from the handler. Additional parameters for
 *  handler can be passed on after the '--'. <p/>
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1.1 $
 * @see weka.classifiers.CheckClassifier
 */
public class TestInstances 
  implements Cloneable, Serializable, OptionHandler {

  /** can be used for settting the class attribute index to last 
   * @see #setClassIndex(int) */
  public final static int CLASS_IS_LAST = -1;
  
  /** can be used to avoid generating a class attribute
   * @see #setClassIndex(int) */
  public final static int NO_CLASS = -2;
  
  /** for generating String attributes/classes */
  protected String[] m_Words = new String[]{"The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog"};
  
  /** the name of the relation */
  protected String m_Relation = "Testdata";
  
  /** the seed value */
  protected int m_Seed = 1;
  
  /** the random number generator */
  protected Random m_Random = new Random(m_Seed);
  
  /** the number of instances */
  protected int m_NumInstances = 20;
  
  /** the class type */
  protected int m_ClassType = Attribute.NOMINAL;
  
  /** the number of classes (in case of NOMINAL class) */
  protected int m_NumClasses = 2;

  /** the class index (-1 is LAST, -2 means no class) 
   * @see #CLASS_IS_LAST
   * @see #NO_CLASS */
  protected int m_ClassIndex = CLASS_IS_LAST;
  
  /** the number of nominal attributes */
  protected int m_NumNominal = 1;
  
  /** the number of values for nominal attributes */
  protected int m_NumNominalValues = 2;
  
  /** the number of numeric attributes */
  protected int m_NumNumeric = 0;
  
  /** the number of string attributes */
  protected int m_NumString = 0;
  
  /** the number of date attributes */
  protected int m_NumDate = 0;
  
  /** the number of relational attributes */
  protected int m_NumRelational = 0;
  
  /** whether to generate Multi-Instance data or not */
  protected boolean m_MultiInstance = false;
  
  /** the number of instances in relational attributes (applies also for bags
   * in multi-instance) */
  protected int m_NumInstancesRelational = 10;
  
  /** the format of the multi-instance data */
  protected Instances[] m_RelationalFormat = null;
  
  /** the format of the multi-instance data of the class */
  protected Instances m_RelationalClassFormat = null;
  
  /** the generated data */
  protected Instances m_Data = null;
  
  /** the CapabilitiesHandler to get the Capabilities from */
  protected CapabilitiesHandler m_Handler = null;

  /**
   * the default constructor
   */
  public TestInstances() {
    super();
    
    setRelation("Testdata");
    setSeed(1);
    setNumInstances(20);
    setClassType(Attribute.NOMINAL);
    setNumClasses(2);
    setClassIndex(CLASS_IS_LAST);
    setNumNominal(1);
    setNumNominalValues(2);
    setNumNumeric(0);
    setNumString(0);
    setNumDate(0);
    setNumRelational(0);
    setNumInstancesRelational(10);
    setMultiInstance(false);
  }
  
  /**
   * creates a clone of the current object
   * 
   * @return		a clone of the current object
   */
  public Object clone() {
    TestInstances     result;
    
    result = new TestInstances();
    result.assign(this);
    
    return result;
  }
  
  /**
   * updates itself with all the settings from the given TestInstances
   * object
   * 
   * @param t		the object to get the settings from
   */
  public void assign(TestInstances t) {
    setRelation(t.getRelation());
    setSeed(t.getSeed());
    setNumInstances(t.getNumInstances());
    setClassType(t.getClassType());
    setNumClasses(t.getNumClasses());
    setClassIndex(t.getClassIndex());
    setNumNominal(t.getNumNominal());
    setNumNominalValues(t.getNumNominalValues());
    setNumNumeric(t.getNumNumeric());
    setNumString(t.getNumString());
    setNumDate(t.getNumDate());
    setNumRelational(t.getNumRelational());
    setMultiInstance(t.getMultiInstance());
    for (int i = 0; i < t.getNumRelational(); i++)
      setRelationalFormat(i, t.getRelationalFormat(i));
    setRelationalClassFormat(t.getRelationalClassFormat());
    setNumInstancesRelational(t.getNumInstancesRelational());
  }
  
  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result = new Vector();
    
    result.addElement(new Option(
        "\tThe name of the data set.",
        "relation", 1, "-relation <name>"));
    
    result.addElement(new Option(
        "\tThe seed value.",
        "seed", 1, "-seed <num>"));
    
    result.addElement(new Option(
        "\tThe number of instances in the datasets (default 20).",
        "num-instances", 1, "-num-instances <num>"));
    
    result.addElement(new Option(
        "\tThe class type, see constants in weka.core.Attribute\n"
	+ "\t(default 1=nominal).",
        "class-type", 1, "-class-type <num>"));
    
    result.addElement(new Option(
        "\tThe number of classes to generate (for nominal classes only)\n"
	+ "\t(default 2).",
        "class-values", 1, "-classes-values <num>"));
    
    result.addElement(new Option(
        "\tThe class index, with -1=last, (default -1).",
        "class-index", 1, "-class-index <num>"));
    
    result.addElement(new Option(
        "\tDoesn't include a class attribute in the output.",
        "no-class", 0, "-no-class"));

    result.addElement(new Option(
        "\tThe number of nominal attributes (default 1).",
        "nominal", 1, "-nominal <num>"));
    
    result.addElement(new Option(
        "\tThe number of values for nominal attributes (default 2).",
        "nominal-values", 1, "-nominal-values <num>"));
    
    result.addElement(new Option(
        "\tThe number of numeric attributes (default 0).",
        "numeric", 1, "-numeric <num>"));
    
    result.addElement(new Option(
        "\tThe number of string attributes (default 0).",
        "string", 1, "-string <num>"));
    
    result.addElement(new Option(
        "\tThe number of date attributes (default 0).",
        "date", 1, "-date <num>"));
    
    result.addElement(new Option(
        "\tThe number of relational attributes (default 0).",
        "relational", 1, "-relational <num>"));
    
    result.addElement(new Option(
        "\tThe number of instances in relational/bag attributes (default 10).",
        "num-instances-relational", 1, "-num-instances-relational <num>"));
    
    result.addElement(new Option(
        "\tGenerates multi-instance data.",
        "multi-instance", 0, "-multi-instance"));

    result.addElement(new Option(
        "\tThe Capabilities handler to base the dataset on.\n"
	+ "\tThe other parameters can be used to override the ones\n"
	+ "\tdetermined from the handler. Additional parameters for\n"
	+ "\thandler can be passed on after the '--'.",
        "W", 1, "-W <classname>"));
    
    return result.elements();
  }
  
  /**
   * Parses a given list of options. 
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String      		tmpStr;
    Class			cls;
    CapabilitiesHandler		handler;
    boolean			initialized;
    
    initialized = false;

    tmpStr = Utils.getOption('W', options);
    if (tmpStr.length() > 0) {
      cls = Class.forName(tmpStr);
      if (ClassDiscovery.hasInterface(CapabilitiesHandler.class, cls)) {
	initialized = true;
	handler = (CapabilitiesHandler) cls.newInstance();
	if (handler instanceof OptionHandler)
	  ((OptionHandler) handler).setOptions(Utils.partitionOptions(options));
	setHandler(handler);
	// initialize
	this.assign(forCapabilities(handler.getCapabilities()));
      }
      else {
	throw new IllegalArgumentException("Class '" + tmpStr + "' is not a CapabilitiesHandler!");
      }
    }

    tmpStr = Utils.getOption("relation", options);
    if (tmpStr.length() != 0)
      setRelation(tmpStr);
    else if (!initialized)
      setRelation("Testdata");
    
    tmpStr = Utils.getOption("seed", options);
    if (tmpStr.length() != 0)
      setSeed(Integer.parseInt(tmpStr));
    else if (!initialized)
      setSeed(1);
    
    tmpStr = Utils.getOption("num-instances", options);
    if (tmpStr.length() != 0)
      setNumInstances(Integer.parseInt(tmpStr));
    else if (!initialized)
      setNumInstances(20);
    
    setNoClass(Utils.getFlag("no-class", options));
    
    if (!getNoClass()) {
      tmpStr = Utils.getOption("class-type", options);
      if (tmpStr.length() != 0)
        setClassType(Integer.parseInt(tmpStr));
      else if (!initialized)
        setClassType(Attribute.NOMINAL);

      tmpStr = Utils.getOption("class-values", options);
      if (tmpStr.length() != 0)
        setNumClasses(Integer.parseInt(tmpStr));
      else if (!initialized)
        setNumClasses(2);

      tmpStr = Utils.getOption("class-index", options);
      if (tmpStr.length() != 0)
        setClassIndex(Integer.parseInt(tmpStr));
      else if (!initialized)
        setClassIndex(-1);
    }
    
    tmpStr = Utils.getOption("nominal", options);
    if (tmpStr.length() != 0)
      setNumNominal(Integer.parseInt(tmpStr));
    else if (!initialized)
      setNumNominal(1);
    
    tmpStr = Utils.getOption("nominal-values", options);
    if (tmpStr.length() != 0)
      setNumNominalValues(Integer.parseInt(tmpStr));
    else if (!initialized)
      setNumNominalValues(2);
    
    tmpStr = Utils.getOption("numeric", options);
    if (tmpStr.length() != 0)
      setNumNumeric(Integer.parseInt(tmpStr));
    else if (!initialized)
      setNumNumeric(0);
    
    tmpStr = Utils.getOption("string", options);
    if (tmpStr.length() != 0)
      setNumString(Integer.parseInt(tmpStr));
    else if (!initialized)
      setNumString(0);
    
    tmpStr = Utils.getOption("date", options);
    if (tmpStr.length() != 0)
      setNumDate(Integer.parseInt(tmpStr));
    else if (!initialized)
      setNumDate(0);
    
    tmpStr = Utils.getOption("relational", options);
    if (tmpStr.length() != 0)
      setNumRelational(Integer.parseInt(tmpStr));
    else if (!initialized)
      setNumRelational(0);
    
    tmpStr = Utils.getOption("num-instances-relational", options);
    if (tmpStr.length() != 0)
      setNumInstancesRelational(Integer.parseInt(tmpStr));
    else if (!initialized)
      setNumInstancesRelational(10);
    
    if (!initialized)
      setMultiInstance(Utils.getFlag("multi-instance", options));
  }
  
  /**
   * Gets the current settings of this object.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    Vector 	result;
    String[]	options;
    int		i;

    result = new Vector();
    
    result.add("-relation");
    result.add(getRelation());
    
    result.add("-seed");
    result.add("" + getSeed());
    
    result.add("-num-instances");
    result.add("" + getNumInstances());
    
    if (getNoClass()) {
      result.add("-no-class");
    }
    else {
      result.add("-class-type");
      result.add("" + getClassType());
      
      result.add("-class-values");
      result.add("" + getNumClasses());
      
      result.add("-class-index");
      result.add("" + getClassIndex());
    }
    
    result.add("-nominal");
    result.add("" + getNumNominal());
    
    result.add("-nominal-values");
    result.add("" + getNumNominalValues());
    
    result.add("-numeric");
    result.add("" + getNumNumeric());
    
    result.add("-string");
    result.add("" + getNumString());
    
    result.add("-date");
    result.add("" + getNumDate());
    
    result.add("-relation");
    result.add("" + getNumRelational());
    
    result.add("-num-instances-relational");
    result.add("" + getNumInstancesRelational());

    if (getMultiInstance())
      result.add("-multi-instance");

    if (getHandler() != null) {
      result.add("-W");
      result.add(getHandler().getClass().getName());
      if (getHandler() instanceof OptionHandler) {
	result.add("--");
	options = ((OptionHandler) getHandler()).getOptions();
	for (i = 0; i < options.length; i++)
	  result.add(options[i]);
      }
    }
    
    return (String[]) result.toArray(new String[result.size()]);
  }
  
  /**
   * sets the name of the relation
   */
  public void setRelation(String value) {
    m_Relation = value;
  }
  
  /**
   * returns the current name of the relation
   */
  public String getRelation() {
    return m_Relation;
  }
  
  /**
   * sets the seed value for the random number generator
   */
  public void setSeed(int value) {
    m_Seed   = value;
    m_Random = new Random(m_Seed);
  }
  
  /**
   * returns the current seed value
   */
  public int getSeed() {
    return m_Seed;
  }
  
  /**
   * sets the number of instances to produce
   */
  public void setNumInstances(int value) {
    m_NumInstances = value;
  }
  
  /**
   * returns the current number of instances to produce
   */
  public int getNumInstances() {
    return m_NumInstances;
  }
  
  /**
   * sets the class attribute type
   */
  public void setClassType(int value) {
    m_ClassType = value;
    m_RelationalClassFormat = null;
  }
  
  /**
   * returns the current class type
   */
  public int getClassType() {
    return m_ClassType;
  }
  
  /**
   * sets the number of classes
   */
  public void setNumClasses(int value) {
    m_NumClasses = value;
  }
  
  /**
   * returns the current number of classes
   */
  public int getNumClasses() {
    return m_NumClasses;
  }
  
  /**
   * sets the class index (0-based)
   * 
   * @see #CLASS_IS_LAST
   * @see #NO_CLASS
   */
  public void setClassIndex(int value) {
    m_ClassIndex = value;
  }
  
  /**
   * returns the current class index (0-based), -1 is last attribute
   * 
   * @see #CLASS_IS_LAST
   * @see #NO_CLASS
   */
  public int getClassIndex() {
    return m_ClassIndex;
  }
  
  /**
   * whether to have no class, e.g., for clusterers; otherwise the class
   * attribute index is set to last
   * 
   * @see #CLASS_IS_LAST
   * @see #NO_CLASS
   */
  public void setNoClass(boolean value) {
    if (value)
      setClassIndex(NO_CLASS);
    else
      setClassIndex(CLASS_IS_LAST);
  }
  
  /**
   * whether no class attribute is generated
   */
  public boolean getNoClass() {
    return (getClassIndex() == NO_CLASS);
  }
  
  /**
   * sets the number of nominal attributes
   */
  public void setNumNominal(int value) {
    m_NumNominal = value;
  }
  
  /**
   * returns the current number of nominal attributes
   */
  public int getNumNominal() {
    return m_NumNominal;
  }
  
  /**
   * sets the number of values for nominal attributes
   */
  public void setNumNominalValues(int value) {
    m_NumNominalValues = value;
  }
  
  /**
   * returns the current number of values for nominal attributes
   */
  public int getNumNominalValues() {
    return m_NumNominalValues;
  }
  
  /**
   * sets the number of numeric attributes
   */
  public void setNumNumeric(int value) {
    m_NumNumeric = value;
  }
  
  /**
   * returns the current number of numeric attributes
   */
  public int getNumNumeric() {
    return m_NumNumeric;
  }
  
  /**
   * sets the number of string attributes
   */
  public void setNumString(int value) {
    m_NumString = value;
  }
  
  /**
   * returns the current number of string attributes
   */
  public int getNumString() {
    return m_NumString;
  }
  
  /**
   * sets the number of data attributes
   */
  public void setNumDate(int value) {
    m_NumDate = value;
  }
  
  /**
   * returns the current number of date attributes
   */
  public int getNumDate() {
    return m_NumDate;
  }
  
  /**
   * sets the number of relational attributes
   */
  public void setNumRelational(int value) {
    m_NumRelational = value;
    m_RelationalFormat = new Instances[value];
  }
  
  /**
   * returns the current number of relational attributes
   */
  public int getNumRelational() {
    return m_NumRelational;
  }
  
  /**
   * sets the number of instances in relational/bag attributes to produce
   */
  public void setNumInstancesRelational(int value) {
    m_NumInstancesRelational = value;
  }
  
  /**
   * returns the current number of instances in relational/bag attributes to produce
   */
  public int getNumInstancesRelational() {
    return m_NumInstancesRelational;
  }

  /**
   * sets whether multi-instance data should be generated (with a fixed
   * data structure)
   */
  public void setMultiInstance(boolean value) {
    m_MultiInstance = value;
  }
  
  /**
   * Gets whether multi-instance data (with a fixed structure) is generated
   */
  public boolean getMultiInstance() {
    return m_MultiInstance;
  }
  
  /**
   * sets the structure for the bags for the relational attribute
   * 
   * @param index       the index of the relational attribute
   * @param value       the new structure
   */
  public void setRelationalFormat(int index, Instances value) {
    if (value != null)
      m_RelationalFormat[index] = new Instances(value, 0);
    else
      m_RelationalFormat[index] = null;
  }
  
  /**
   * returns the format for the specified relational attribute, can be null
   * 
   * @param index       the index of the relational attribute
   * @return            the current structure
   */
  public Instances getRelationalFormat(int index) {
    return m_RelationalFormat[index];
  }

  /**
   * sets the structure for the relational class attribute
   */
  public void setRelationalClassFormat(Instances value) {
    if (value != null)
      m_RelationalClassFormat = new Instances(value, 0);
    else
      m_RelationalClassFormat = null;
  }
  
  /**
   * returns the current strcuture of the relational class attribute, can
   * be null
   */
  public Instances getRelationalClassFormat() {
    return m_RelationalClassFormat;
  }
  
  /**
   * returns the overall number of attributes (incl. class, if that is also
   * generated)
   */
  public int getNumAttributes() {
    int		result;
    
    result = m_NumNominal + m_NumNumeric + m_NumString + m_NumDate + m_NumRelational;
    
    if (!getNoClass())
      result++;
      
    return result;
  }
  
  /**
   * returns the current dataset, can be null
   */
  public Instances getData() {
    return m_Data;
  }
  
  /**
   * sets the Capabilities handler to generate the data for
   */
  public void setHandler(CapabilitiesHandler value) {
    m_Handler = value;
  }
  
  /**
   * returns the current set CapabilitiesHandler to generate the dataset
   * for, can be null
   */
  public CapabilitiesHandler getHandler() {
    return m_Handler;
  }
  
  /**
   * creates a new Attribute of the given type
   * 
   * @param index the index of the current attribute (0-based)
   * @param attType the attribute type (NUMERIC, NOMINAL, etc.)
   * @return the configured attribute
   * 
   * @see Attribute#type()
   * @see #CLASS_IS_LAST
   * @see #NO_CLASS
   */
  protected Attribute generateAttribute(int index, int attType) throws Exception {
    Attribute     result;
    String        name;
    int           valIndex;
    int           nomCount;
    String        prefix;
    
    result = null;

    // determine name and start-index
    if (index == CLASS_IS_LAST) {
      valIndex = 0;
      name     = "Class";
      prefix   = "class";
      nomCount = getNumClasses();
    }
    else {
      valIndex = index;
      nomCount = getNumNominalValues();
      prefix   = "att" + (valIndex + 1) + "val";
      
      switch (attType) {
        case Attribute.NOMINAL:
          name = "Nominal" + (valIndex + 1);
          break;
          
        case Attribute.NUMERIC:
          name = "Numeric" + (valIndex + 1);
          break;
          
        case Attribute.STRING:
          name = "String" + (valIndex + 1);
          break;
          
        case Attribute.DATE:
          name = "Date" + (valIndex + 1);
          break;
          
        case Attribute.RELATIONAL:
          name = "Relational" + (valIndex + 1);
          break;
          
        default:
          throw new IllegalArgumentException("Attribute type '" + attType + "' unknown!");
      }
    }
    
    switch (attType) {
      case Attribute.NOMINAL:
        FastVector nomStrings = new FastVector(valIndex + 1);
        for (int j = 0; j < nomCount; j++)
          nomStrings.addElement(prefix + (j + 1));
        result = new Attribute(name, nomStrings);
        break;
        
      case Attribute.NUMERIC:
        result = new Attribute(name);
        break;
        
      case Attribute.STRING:
        result = new Attribute(name, (FastVector) null);
        break;
        
      case Attribute.DATE:
        result = new Attribute(name, "yyyy-mm-dd");
        break;
        
      case Attribute.RELATIONAL:
        Instances rel;
        if (index == CLASS_IS_LAST)
          rel = getRelationalClassFormat();
        else
          rel = getRelationalFormat(index);
        
        if (rel == null) {
          TestInstances dataset = (TestInstances) this.clone();
          dataset.setMultiInstance(false);
          dataset.setNumRelational(0);
          dataset.setNumInstances(0);
          dataset.setClassType(Attribute.NOMINAL);  // dummy to avoid endless recursion, will be deleted anyway
          rel = new Instances(dataset.generate());
          if (!getNoClass()) {
            int clsIndex = rel.classIndex();
            rel.setClassIndex(-1);
            rel.deleteAttributeAt(clsIndex);
          }
        }
        result = new Attribute(name, rel);
        break;
        
      default:
        throw new IllegalArgumentException("Attribute type '" + attType + "' unknown!");
    }
    
    return result;
  }
  
  /**
   * Generates the class value
   * 
   * @param data  the dataset to work on
   * @return      the new class value
   */
  protected double generateClassValue(Instances data) throws Exception {
    double result = Double.NaN;
    
    switch (m_ClassType) {
      case Attribute.NUMERIC:
        result = m_Random.nextFloat() * 0.25
            + Math.abs(m_Random.nextInt())
            % Math.max(2, m_NumNominal);
        break;
        
      case Attribute.NOMINAL:
        result = Math.abs(m_Random.nextInt()) % data.numClasses();
        break;
        
      case Attribute.STRING:
        String str = "";
        for (int n = 0; n < m_Words.length; n++) {
          if (n > 0)
            str += " ";
          str += m_Words[m_Random.nextInt(m_Words.length)];
        }
        result = data.classAttribute().addStringValue(str);
        break;
        
      case Attribute.DATE:
        result = data.classAttribute().parseDate(
                (2000 + m_Random.nextInt(100)) + "-01-01");
        break;
        
      case Attribute.RELATIONAL:
        if (getRelationalClassFormat() != null) {
          result = data.classAttribute().addRelation(getRelationalClassFormat());
        }
        else {
          TestInstances dataset = (TestInstances) this.clone();
          dataset.setMultiInstance(false);
          dataset.setNumRelational(0);
          dataset.setClassType(Attribute.NOMINAL);  // dummy to avoid endless recursion, will be deleted anyway
          Instances rel = new Instances(dataset.generate());
          int clsIndex = rel.classIndex();
          rel.setClassIndex(-1);
          rel.deleteAttributeAt(clsIndex);
          result = data.classAttribute().addRelation(rel);
        }
        break;
    }
    
    return result;
  }
  
  /**
   * Generates a new value for the specified attribute. The classValue
   * might be used in the process.
   * 
   * @param data          the dataset to work on
   * @param index         the index of the attribute
   * @param classVal      the class value for the current instance, might be 
   *                      used in the calculation
   * @return              the new attribute value
   */
  protected double generateAttributeValue(Instances data, int index, double classVal) throws Exception {
    double result = Double.NaN;
    
    switch (data.attribute(index).type()) {
      case Attribute.NUMERIC:
        result = classVal * 4 + m_Random.nextFloat() * 1 - 0.5;
        break;
        
      case Attribute.NOMINAL:
        if (m_Random.nextFloat() < 0.2) {
          result = Math.abs(m_Random.nextInt())
          % data.attribute(index).numValues();
        } else {
          result = ((int)classVal) % data.attribute(index).numValues();
        }
	//result = m_Random.nextInt(data.attribute(index).numValues());
        break;
        
      case Attribute.STRING:
        String str = "";
        for (int n = 0; n < m_Words.length; n++) {
          if (n > 0)
            str += " ";
          str += m_Words[m_Random.nextInt(m_Words.length)];
        }
        result = data.attribute(index).addStringValue(str);
        break;
        
      case Attribute.DATE:
        result = data.attribute(index).parseDate(
                (2000 + m_Random.nextInt(100)) + "-01-01");
        break;
        
      case Attribute.RELATIONAL:
        Instances rel = new Instances(data.attribute(index).relation(), 0);
        for (int n = 0; n < getNumInstancesRelational(); n++) {
          Instance inst = new Instance(rel.numAttributes());
          inst.setDataset(data);
          for (int i = 0; i < rel.numAttributes(); i++) {
            inst.setValue(i, generateAttributeValue(rel, i, 0));
          }
          rel.add(inst);
        }
        result = data.attribute(index).addRelation(rel);
        break;
    }
    
    return result;
  }
  
  /**
   * generates a new dataset.
   */
  public Instances generate() throws Exception {
    if (getMultiInstance()) {
      TestInstances bag = (TestInstances) this.clone();
      bag.setMultiInstance(false);
      bag.setNumInstances(0);
      bag.setSeed(m_Random.nextInt());
      Instances bagFormat = bag.generate();
      bagFormat.setClassIndex(-1);
      bagFormat.deleteAttributeAt(bagFormat.numAttributes() - 1);

      // generate multi-instance structure
      TestInstances structure = new TestInstances();
      structure.setSeed(m_Random.nextInt());
      structure.setNumNominal(1);
      structure.setNumRelational(1);
      structure.setRelationalFormat(0, bagFormat);
      structure.setClassType(getClassType());
      structure.setNumClasses(getNumClasses());
      structure.setRelationalClassFormat(getRelationalClassFormat());
      structure.setNumInstances(getNumInstances());
      m_Data = structure.generate();
      
      // generate bags
      bag.setNumInstances(getNumInstancesRelational());
      for (int i = 0; i < getNumInstances(); i++) {
        bag.setSeed(m_Random.nextInt());
        Instances bagData = new Instances(bag.generate());
        bagData.setClassIndex(-1);
        bagData.deleteAttributeAt(bagData.numAttributes() - 1);
        double val = m_Data.attribute(1).addRelation(bagData);
        m_Data.instance(i).setValue(1, val);
      }
    }
    else {
      // initialize
      int clsIndex = m_ClassIndex;
      if (clsIndex == CLASS_IS_LAST)
        clsIndex = getNumAttributes() - 1;

      // generate attributes
      FastVector attributes = new FastVector(getNumAttributes());
      // Add Nominal attributes
      for (int i = 0; i < getNumNominal(); i++)
        attributes.addElement(generateAttribute(i, Attribute.NOMINAL));
      
      // Add m_Numeric attributes
      for (int i = 0; i < getNumNumeric(); i++)
        attributes.addElement(generateAttribute(i, Attribute.NUMERIC));
      
      // Add some String attributes...
      for (int i = 0; i < getNumString(); i++)
        attributes.addElement(generateAttribute(i, Attribute.STRING));
      
      // Add some Date attributes...
      for (int i = 0; i < getNumDate(); i++)
        attributes.addElement(generateAttribute(i, Attribute.DATE));
      
      // Add some Relational attributes...
      for (int i = 0; i < getNumRelational(); i++)
        attributes.addElement(generateAttribute(i, Attribute.RELATIONAL));
      
      // Add class attribute
      if (clsIndex != NO_CLASS)
	attributes.insertElementAt(generateAttribute(CLASS_IS_LAST, getClassType()), clsIndex);
      
      m_Data = new Instances(getRelation(), attributes, getNumInstances());
      m_Data.setClassIndex(clsIndex);

      // generate instances
      for (int i = 0; i < getNumInstances(); i++) {
        Instance current = new Instance(getNumAttributes());
        current.setDataset(m_Data);

        // class
        double classVal;
        if (clsIndex != NO_CLASS) {
          classVal = generateClassValue(m_Data);
          current.setClassValue(classVal);
        }
        else {
          classVal = m_Random.nextFloat();
        }
        
        // other attributes
        for (int n = 0; n < getNumAttributes(); n++) {
          if (clsIndex == n)
            continue;
          
          current.setValue(n, generateAttributeValue(m_Data, n, classVal));
        }
        
        m_Data.add(current);
      }
    }
    
    return getData();
  }
  
  /**
   * returns a TestInstances instance setup already for the the given
   * capabilities.
   * 
   * @param c		the capabilities to base the TestInstances on
   * @return		the configured TestInstances object
   */
  public static TestInstances forCapabilities(Capabilities c) {
    TestInstances	result;
    
    result = new TestInstances();
    
    // multi-instance?
    if (c.getOwner() instanceof MultiInstanceCapabilitiesHandler) {
      result = forCapabilities(((MultiInstanceCapabilitiesHandler) c.getOwner()).getMultiInstanceCapabilities());
      result.setMultiInstance(true);
    }
    else  {
      // class
      if (c.handles(Capability.NO_CLASS))
	result.setClassIndex(NO_CLASS);
      else if (c.handles(Capability.NOMINAL_CLASS))
	result.setClassType(Attribute.NOMINAL);
      else if (c.handles(Capability.BINARY_CLASS))
	result.setClassType(Attribute.NOMINAL);
      else if (c.handles(Capability.NUMERIC_CLASS))
	result.setClassType(Attribute.NUMERIC);
      else if (c.handles(Capability.DATE_CLASS))
	result.setClassType(Attribute.DATE);
      else if (c.handles(Capability.STRING_CLASS))
	result.setClassType(Attribute.STRING);
      else if (c.handles(Capability.RELATIONAL_CLASS))
	result.setClassType(Attribute.RELATIONAL);

      // # of classes
      if (c.handles(Capability.BINARY_CLASS))
	result.setNumClasses(2);
      if (c.handles(Capability.NOMINAL_CLASS))
	result.setNumClasses(4);
      
      // attributes
      if (c.handles(Capability.NOMINAL_ATTRIBUTES))
	result.setNumNominal(1);
      if (c.handles(Capability.NUMERIC_ATTRIBUTES))
	result.setNumNumeric(1);
      if (c.handles(Capability.DATE_ATTRIBUTES))
	result.setNumDate(1);
      if (c.handles(Capability.STRING_ATTRIBUTES))
	result.setNumString(1);
      if (c.handles(Capability.RELATIONAL_ATTRIBUTES))
	result.setNumRelational(1);
    }
    
    return result;
  }
  
  /**
   * returns a string representation of the object
   */
  public String toString() {
    String	result;
    
    result = "";
    result += "Relation: " + getRelation() + "\n";
    result += "Seed: " + getSeed() + "\n";
    result += "# Instances: " + getNumInstances() + "\n";
    result += "ClassType: " + getClassType() + "\n";
    result += "# Classes: " + getNumClasses() + "\n";
    result += "Class index: " + getClassIndex() + "\n";
    result += "# Nominal: " +     getNumNominal() + "\n";
    result += "# Nominal values: " + getNumNominalValues() + "\n";
    result += "# Numeric: " + getNumNumeric() + "\n";
    result += "# String: " + getNumString() + "\n";
    result += "# Date: " + getNumDate() + "\n";
    result += "# Relational: " + getNumRelational() + "\n";
    result += "# Relational Instances: " + getNumInstancesRelational() + "\n";
    result += "Multi-Instance: " + getMultiInstance() + "\n";
    
    return result;
  }
  
  /**
   * for running the class from commandline, prints the generated data
   * to stdout
   * 
   * @param args	the commandline parameters
   */
  public static void main(String[] args) throws Exception {
    TestInstances inst;
    
    inst = new TestInstances();

    // help requested?
    if (Utils.getFlag("h", args) || Utils.getFlag("help", args)) {
      StringBuffer result = new StringBuffer();
      result.append("\nTest data generator options:\n\n");

      result.append("-h|-help\n\tprints this help\n");
      
      Enumeration enm = inst.listOptions();
      while (enm.hasMoreElements()) {
        Option option = (Option) enm.nextElement();
        result.append(option.synopsis() + "\n" + option.description() + "\n");
      }

      System.out.println(result);
      System.exit(0);
    }
    
    // generate data
    inst.setOptions(args);
    System.out.println(inst.generate());
  }
}
