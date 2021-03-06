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
 * Copyright (C) 2002 University of Waikato 
 */

package weka.classifiers.meta;

import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.Classifier;

/**
 * Dummy classifier - used in ThresholdSelectorTest.
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @author FracPete (fracpet at waikato dor ac dot nz)
 * @version $Revision: 1.1 $
 * @see ThresholdSelectorTest
 */

public class ThresholdSelectorDummyClassifier 
  extends Classifier {

  private double[] m_Preds;
  private int m_Pos;

  public ThresholdSelectorDummyClassifier(double[] preds) {
    m_Preds = new double[preds.length];
    for (int i = 0; i < preds.length; i++)
      m_Preds[i] = preds[i];
  }

  public void buildClassifier(Instances train) { 
  }

  public double[] distributionForInstance(Instance test) throws Exception {
    double[] result = new double[test.numClasses()];
    int pred = 0;
    result[pred] = m_Preds[m_Pos];
    double residual = (1.0 - result[pred]) / (result.length - 1);
    for (int i = 0; i < result.length; i++) {
      if (i != pred) {
        result[i] = residual;
      }
    }
    m_Pos = (m_Pos + 1) % m_Preds.length;
    return result;
  }
}

