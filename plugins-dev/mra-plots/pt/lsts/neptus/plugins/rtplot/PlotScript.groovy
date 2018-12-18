/*
 * Copyright (c) 2004-2018 Universidade do Porto - Faculdade de Engenharia
 * Laboratório de Sistemas e Tecnologia Subaquática (LSTS)
 * All rights reserved.
 * Rua Dr. Roberto Frias s/n, sala I203, 4200-465 Porto, Portugal
 *
 * This file is part of Neptus, Command and Control Framework.
 *
 * Commercial Licence Usage
 * Licencees holding valid commercial Neptus licences may use this file
 * in accordance with the commercial licence agreement provided with the
 * Software or, alternatively, in accordance with the terms contained in a
 * written agreement between you and Universidade do Porto. For licensing
 * terms, conditions, and further information contact lsts@fe.up.pt.
 *
 * Modified European Union Public Licence - EUPL v.1.1 Usage
 * Alternatively, this file may be used under the terms of the Modified EUPL,
 * Version 1.1 only (the "Licence"), appearing in the file LICENSE.md
 * included in the packaging of this file. You may not use this work
 * except in compliance with the Licence. Unless required by applicable
 * law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations at
 * https://github.com/LSTS/neptus/blob/develop/LICENSE.md
 * and http://ec.europa.eu/idabc/eupl.html.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 * Author: keila
 * Dec 12, 2018
 */
package pt.lsts.neptus.plugins.rtplot

import java.awt.BorderLayout
import java.awt.geom.Point2D
import java.util.Map;

import pt.lsts.imc.*;
import pt.lsts.neptus.NeptusLog
import pt.lsts.neptus.comm.manager.imc.ImcSystem
import pt.lsts.neptus.comm.manager.imc.ImcMsgManager
import pt.lsts.neptus.comm.manager.imc.ImcSystemsHolder
import pt.lsts.neptus.mra.plots.TimedXYDataItem
import pt.lsts.neptus.plugins.rtplot.RealTimePlotGroovy.PlotType
import pt.lsts.neptus.types.coord.LocationType
import pt.lsts.neptus.util.GuiUtils

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond
import org.jfree.data.time.TimeSeries
import org.jfree.data.time.TimeSeriesCollection
import org.jfree.data.time.TimeSeriesDataItem
import org.jfree.data.xy.XYDataItem
import org.jfree.data.xy.XYSeries



class PlotScript {
    static systems = []     //systems variable must be declared and updated on the script evaluation
    static RealTimePlotGroovy plot = null
    static def state = {field -> systems.collectEntries{ [(it): ImcMsgManager.getManager().getState(it).get("EstimatedState."+field)]}}
    //TODO add to @RealTimePlotScript Options JMenu
    //static final def rollscript  = state("phi").apply()//{state.each {k,v -> [(k):v.phi* 180/Math.PI]}}
    static final def pitchscript = {state.each {k,v -> [(k):v.theta* 180/Math.PI]}}
    static final def yawscript   = {state.each {k,v -> [(k):v.psi* 180/Math.PI]}}
    //ImcMsgManager.getManager().getState(it).get(msg).get(field, Number)
    static def msgs = { msgDotField ->
        try {
            systems.collectEntries{ [(it+"."+msgDotField): ImcMsgManager.getManager().getState(it).expr(msgDotField)]}
        } catch (IOException e) {
            GuiUtils.errorMessage(this, "Error parsing scritp msgs method:", e.getLocalizedMessage());
        }
    }

    static def apply = {LinkedHashMap map, Object f -> map.each { [(it.key):function.call(it.value)]}}

    static TimeSeries x() {}

    static TimeSeries y() {}

    static def addTimeSerie(LinkedHashMap map) {
        if(!plot.getType().equals(PlotType.TIMESERIES)) {
            //plot.resetSeries()
            plot.setType(PlotType.TIMESERIES)
        }
        if(systems.size()>0) {
            map.each {
                TimeSeriesDataItem item = new TimeSeriesDataItem(new Millisecond(new Date(System.currentTimeMillis())),new Double(it.value))
                TimeSeries t = new TimeSeries(it.key)
                t.add(item)
                plot.addTimeSerie(it.key,t)
            }
        }
    }
    static def addSerie(LinkedHashMap map) {
        if(systems.size()>0) {
            map.each {
                XYSeries xy = new XYSeries(it.key,false)
                xy.add(it.value);
                plot.addSerie(it.key,xy)
            }
        }
    }

    static def plotLatLong() {
        systems.each {
            EstimatedState state = ImcMsgManager.getManager().getState(it).get("EstimatedState")
            LocationType ref = new LocationType(0,0)
            if(!plot.getType().equals(PlotType.LATLONG)) {
                //plot.resetSeries()
                plot.setType(PlotType.LATLONG)
            }
            def resultmap = [:]
            LocationType loc =  new LocationType(state.getDouble("lat"),state.getDouble("lon")) //lat long
            loc.translatePosition(state.getDouble("x"), state.getDouble("y"), state.getDouble("z"))
            def id = it+".position"
            double[] offsets = loc.getOffsetFrom(ref)
            //TimedXYDataItem item =  new TimedXYDataItem(offsets[0],offsets[1],System.currentTimeMillis(),id)
            Point2D pt = new Point2D.Double(offsets[0],offsets[1])
            XYDataItem item = new XYDataItem(pt.getX(),pt.getY())
            resultmap.put id, item
            addSerie(resultmap)
        }
    }

}