/*
 *    TimeSeriesTranslateFilter.java
 *    Copyright (C) 1999 Len Trigg
 *
 */


package weka.filters;

import java.io.*;
import java.util.*;
import weka.core.*;

/** 
 * An instance filter that assumes instances form time-series data and
 * replaces attribute values in the current instance with the equivalent
 * attribute attribute values of some previous (or future) instance. For
 * instances where the desired value is unknown either the instance may
 * be dropped, or missing values used.<p>
 *
 * Valid filter-specific options are:<p>
 *
 * -R index1,index2-index4,...<br>
 * Specify list of columns to calculate new values for.
 * First and last are valid indexes.
 * (default none)<p>
 *
 * -V <br>
 * Invert matching sense (i.e. calculate for all non-specified columns)<p>
 *
 * -I num <br>
 * The number of instances forward to translate values between.
 * A negative number indicates taking values from a past instance.
 * (default -1) <p>
 *
 * -M <br>
 * For instances at the beginning or end of the dataset where the translated
 * values are not known, use missing values (default is to remove those
 * instances). <p>
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision: 1.5 $
 */
public class TimeSeriesTranslateFilter extends Filter
  implements OptionHandler {

  /** Stores which columns to copy */
  protected Range m_SelectedCols = new Range();

  /**
   * True if missing values should be used rather than removing instances
   * where the translated value is not known (due to border effects).
   */
  protected boolean m_FillWithMissing;

  /**
   * The number of instances forward to translate values between.
   * A negative number indicates taking values from a past instance.
   */
  protected int m_InstanceRange = -1;

  /** Stores the historical instances to copy values between */
  protected Queue m_History;;
  
  /**
   * Returns an enumeration describing the available options
   *
   * @return an enumeration of all the available options
   */
  public Enumeration listOptions() {

    Vector newVector = new Vector(4);

    newVector.addElement(new Option(
              "\tSpecify list of columns to translate in time. First and\n"
	      + "\tlast are valid indexes. (default none)",
              "R", 1, "-R <index1,index2-index4,...>"));
    newVector.addElement(new Option(
	      "\tInvert matching sense (i.e. copy all non-specified columns)",
              "V", 0, "-V"));
    newVector.addElement(new Option(
              "\tThe number of instances forward to translate values\n"
	      + "\tbetween. A negative number indicates taking values from\n"
	      + "\ta past instance. (default -1)",
              "I", 1, "-I <num>"));
    newVector.addElement(new Option(
	      "\tFor instances at the beginning or end of the dataset where\n"
	      + "\tthe translated values are not known, use missing values\n"
	      + "\t(default is to remove those instances).",
              "M", 0, "-M"));

    return newVector.elements();
  }

  /**
   * Parses a given list of options controlling the behaviour of this object.
   * Valid options are:<p>
   *
   * -R index1,index2-index4,...<br>
   * Specify list of columns to copy. First and last are valid indexes.
   * (default none)<p>
   *
   * -V<br>
   * Invert matching sense (i.e. copy all non-specified columns)<p>
   *
   * -I num <br>
   * The number of instances forward to translate values between.
   * A negative number indicates taking values from a past instance.
   * (default -1) <p>
   *
   * -M <br>
   * For instances at the beginning or end of the dataset where the translated
   * values are not known, use missing values (default is to remove those
   * instances). <p>
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {

    String copyList = Utils.getOption('R', options);
    if (copyList.length() != 0) {
      setAttributeIndices(copyList);
    } else {
      setAttributeIndices("");
    }
    
    setInvertSelection(Utils.getFlag('V', options));

    setFillWithMissing(Utils.getFlag('M', options));
    
    String instanceRange = Utils.getOption('I', options);
    if (instanceRange.length() != 0) {
      setInstanceRange(Integer.parseInt(instanceRange));
    } else {
      setInstanceRange(-1);
    }

    if (getInputFormat() != null) {
      inputFormat(getInputFormat());
    }
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {

    String [] options = new String [6];
    int current = 0;

    if (!getAttributeIndices().equals("")) {
      options[current++] = "-R"; options[current++] = getAttributeIndices();
    }
    if (getInvertSelection()) {
      options[current++] = "-V";
    }
    options[current++] = "-I"; options[current++] = "" + getInstanceRange();
    if (getFillWithMissing()) {
      options[current++] = "-M";
    }

    while (current < options.length) {
      options[current++] = "";
    }
    return options;
  }

  /**
   * Sets the format of the input instances.
   *
   * @param instanceInfo an Instances object containing the input instance
   * structure (any instances contained in the object are ignored - only the
   * structure is required).
   * @return true if the outputFormat may be collected immediately
   * @exception Exception if the format couldn't be set successfully
   */
  public boolean inputFormat(Instances instanceInfo) throws Exception {

    super.inputFormat(instanceInfo);
    resetHistory();
    m_SelectedCols.setUpper(instanceInfo.numAttributes() - 1);
    // Create the output buffer
    Instances outputFormat = new Instances(instanceInfo, 0); 
    for(int i = 0; i < instanceInfo.numAttributes(); i++) {
      if (m_SelectedCols.isInRange(i)) {
	if (outputFormat.attribute(i).isNominal()
	    || outputFormat.attribute(i).isNumeric()) {
	  outputFormat.renameAttribute(i, outputFormat.attribute(i).name()
				       + (m_InstanceRange < 0 ? '-' : '+')
				       + Math.abs(m_InstanceRange));
	} else {
	  throw new Exception("Only numeric and nominal attributes may be "
			      + " manipulated in time series.");
	}
      }
    }
    setOutputFormat(outputFormat);
    return true;
  }
  

  /**
   * Input an instance for filtering. Ordinarily the instance is processed
   * and made available for output immediately. Some filters require all
   * instances be read before producing output.
   *
   * @param instance the input instance
   * @return true if the filtered instance may now be
   * collected with output().
   * @exception Exception if the input instance was not of the correct 
   * format or if there was a problem with the filtering.
   */
  public boolean input(Instance instance) throws Exception {

    if (getInputFormat() == null) {
      throw new Exception("No input instance format defined");
    }
    if (m_NewBatch) {
      resetQueue();
      m_NewBatch = false;
      resetHistory();
    }

    Instance newInstance = historyInput(instance);
    if (newInstance != null) {
      push(newInstance);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Signifies that this batch of input to the filter is finished. If the 
   * filter requires all instances prior to filtering, output() may now 
   * be called to retrieve the filtered instances.
   *
   * @return true if there are instances pending output
   * @exception Exception if no input structure has been defined
   */
  public boolean batchFinished() throws Exception {

    if (getInputFormat() == null) {
      throw new Exception("No input instance format defined");
    }
    if (getFillWithMissing() && (m_InstanceRange > 0)) {
      while (!m_History.empty()) {
	push(mergeInstances(null, (Instance) m_History.pop()));
      }
    } 
    m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  /**
   * Gets whether missing values should be used rather than removing instances
   * where the translated value is not known (due to border effects).
   *
   * @return true if so
   */
  public boolean getFillWithMissing() {
    
    return m_FillWithMissing;
  }
  
  /**
   * Sets whether missing values should be used rather than removing instances
   * where the translated value is not known (due to border effects).
   *
   * @param newFillWithMissing true if so
   */
  public void setFillWithMissing(boolean newFillWithMissing) {
    
    m_FillWithMissing = newFillWithMissing;
  }

  
  
  /**
   * Gets the number of instances forward to translate values between.
   * A negative number indicates taking values from a past instance.
   *
   * @return Value of InstanceRange.
   */
  public int getInstanceRange() {
    
    return m_InstanceRange;
  }
  
  /**
   * Sets the number of instances forward to translate values between.
   * A negative number indicates taking values from a past instance.
   *
   * @param newInstanceRange Value to assign to InstanceRange.
   */
  public void setInstanceRange(int newInstanceRange) {
    
    m_InstanceRange = newInstanceRange;
  }
  
  /**
   * Get whether the supplied columns are to be removed or kept
   *
   * @return true if the supplied columns will be kept
   */
  public boolean getInvertSelection() {

    return m_SelectedCols.getInvert();
  }

  /**
   * Set whether selected columns should be removed or kept. If true the 
   * selected columns are kept and unselected columns are copied. If false
   * selected columns are copied and unselected columns are kept.
   *
   * @param invert the new invert setting
   */
  public void setInvertSelection(boolean invert) {

    m_SelectedCols.setInvert(invert);
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String attributeIndicesTipText() {
    return "Specify range of attributes to act on."
      + " This is a comma separated list of attribute indices, with"
      + " \"first\" and \"last\" valid values. Specify an inclusive"
      + " range with \"-\". E.g: \"first-3,5,6-10,last\".";
  }

  /**
   * Get the current range selection
   *
   * @return a string containing a comma separated list of ranges
   */
  public String getAttributeIndices() {

    return m_SelectedCols.getRanges();
  }

  /**
   * Set which attributes are to be copied (or kept if invert is true)
   *
   * @param rangeList a string representing the list of attributes.  Since
   * the string will typically come from a user, attributes are indexed from
   * 1. <br>
   * eg: first-3,5,6-last
   * @exception Exception if an invalid range list is supplied
   */
  public void setAttributeIndices(String rangeList) throws Exception {

    m_SelectedCols.setRanges(rangeList);
  }

  /**
   * Set which attributes are to be copied (or kept if invert is true)
   *
   * @param attributes an array containing indexes of attributes to select.
   * Since the array will typically come from a program, attributes are indexed
   * from 0.
   * @exception Exception if an invalid set of ranges is supplied
   */
  public void setAttributeIndicesArray(int [] attributes) throws Exception {

    String rangeList = "";
    for(int i = 0; i < attributes.length; i++) {
      if (i == 0) {
	rangeList = "" + (attributes[i] + 1);
      } else {
	rangeList += "," + (attributes[i] + 1);
      }
    }
    setAttributeIndices(rangeList);
  }

  /** Clears any instances from the history queue. */
  protected void resetHistory() {

    if (m_History == null) {
      m_History = new Queue();
    } else {
      m_History.removeAllElements();
    }
  }

  /**
   * Adds an instance to the history buffer. If enough instances are in
   * the buffer, a new instance may be output, with selected attribute
   * values copied from one to another.
   *
   * @param instance the input instance
   * @return a new instance with translated values, or null if no
   * output instance is produced
   * @exception Exception if an error occurs
   */
  protected Instance historyInput(Instance instance) throws Exception {

    m_History.push(instance);
    if (m_History.size() <= Math.abs(m_InstanceRange)) {
      if (getFillWithMissing() && (m_InstanceRange < 0)) {
	return mergeInstances(null, instance);
      } else {
	return null;
      }
    }
    if (m_InstanceRange < 0) {
      return mergeInstances((Instance) m_History.pop(), instance);
    } else {
      return mergeInstances(instance, (Instance) m_History.pop());
    }
  }

  /**
   * Creates a new instance the same as one instance (the "destination")
   * but with some attribute values copied from another instance
   * (the "source")
   *
   * @param source the source instance
   * @param dest the destination instance
   * @return the new merged instance
   * @exception Exception if a problem occurs during merging
   */
  protected Instance mergeInstances(Instance source, Instance dest)
    throws Exception {

    Instances outputFormat = outputFormatPeek();
    double[] vals = new double[outputFormat.numAttributes()];
    for(int i = 0; i < vals.length; i++) {
      if (m_SelectedCols.isInRange(i)) {
	if (source != null) {
	  vals[i] = source.value(i);
	}
      } else {
	vals[i] = dest.value(i);
      }
    }
     if (dest instanceof SparseInstance) {
      return new SparseInstance(dest.weight(), vals);
    } else {
      return new Instance(dest.weight(), vals);
    }
  }
  
  /**
   * Main method for testing this class.
   *
   * @param argv should contain arguments to the filter: use -h for help
   */
  public static void main(String [] argv) {

    try {
      if (Utils.getFlag('b', argv)) {
 	Filter.batchFilterFile(new TimeSeriesTranslateFilter(), argv); 
      } else {
	Filter.filterFile(new TimeSeriesTranslateFilter(), argv);
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }
}








