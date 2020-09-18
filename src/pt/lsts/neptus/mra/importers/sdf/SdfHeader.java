/*
 * Copyright (c) 2004-2014 Universidade do Porto - Faculdade de Engenharia
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
 * Author: Manuel R.
 * Oct 21, 2014
 */
package pt.lsts.neptus.mra.importers.sdf;

import java.nio.ByteBuffer;

public class SdfHeader {
    private long numberBytes; // total number of bytes in page

    /*---------pageVersion-------------------------------
    | UUV 3500 sonar data(Obsolete)              | 3500 |
    | UUV 3500 sonar data Low Frequency          | 3501 |
    | UUV 3500 sonar data High Frequency         | 3502 |
    | UUV 3500 sonar data Bathy Pulse Compressed | 3503 |
    | UUV 3500 sonar data Bathy Processed        | 3511 |
    -----------------------------------------------------
     */
    private long pageVersion; // data page structure

    /*
     * configuration Bit 0 – Channel 0 (LF Port side scan/Bathy) Bit 1 – Channel 1 (LF Stbd side scan/Bathy) Bit 2 –
     * Channel 2 (HF Port side scan) Bit 3 – Channel 3 (HF Stbd side scan) Bit 4 – Channel 4 (LF Bathy) Bit 5 – Channel
     * 5 (LF Bathy) Bit 6 – Channel 6 (LF Bathy) Bit 7 – Channel 7 (LF Bathy) Bits 23-8: Reserved. Bits 31-24: Framing
     * Mode, coded as follows: 0 – Reserved 1 – LF Side Scan Only 2 – HF Side Scan Only 3 – LF and HF Side Scan 4 –
     * Reserved (LF Bathy only) 5 – LF Side Scan and LF Bathy only 6 – LF Bathy and HF Side Scan 7 – LF and HF Side Scan
     * and LF Bathy 8 – 255 – Reserved.
     */
    private long configuration; // Bit field indicates which channels have been enabled for data acquisition
    private long pingNumber; // number of sonar pings
    private long numSamples; // count of samples in processed side-scan data

    /*
     * errorFlags Bit field: Bit 0: Invalid speed Bit 1: GPS data error Bit 2: Telemetry error Bit 3: Sensor Checksum
     * Error Bit 4: Towfish Leak Bit 5: Bad Data Bit 6: Watchdog Bit 7: Compass Error Bit 8: No GPS Lat/Lon sentence
     * input Bit 9: No GPS speed sentence input Bit 10: No GPS ZDA sentence input Bit 11: No data from Motion Reference
     * Unit or MRU error Bit 12: No 1 PPS input Not all bits are valid for all sonar configurations. For example,
     * towfish must be equipped with a leak sensor to set the towfish leak flag.
     */
    private long errorFlags;
    private long rangeMeters;
    private long speedFish; // cm/s

    private long speedSound; // speed of sound at the transducer arrays from a specified source (in cm/s)

    /*
     * Transmit waveform number Bits 0 thru 7 represent the Low Frequency transmit waveform number Bits 8 thru 15
     * represent the High Frequency transmit waveform number
     */
    private long txWaveform;

    private int year; // TPU Time of ping, Calendar Year
    private int month; // TPU Time of ping, Calendar month
    private int day; // TPU Time of ping, Calendar day
    private int hour; // TPU Time of ping, hour
    private int minute; // TPU Time of ping, minute
    private int second; // TPU Time of ping, second
    private int hSecond; // TPU Time of ping, hundredths of second (1-99)
    private float fSecond; // TPU Time of ping, fractional seconds (seconds)

    private float fishHeading; // heading from towfish compass (degrees)
    private float pitch; // pitch from compass (deg.)
    private float roll; // roll from compass (deg.)
    private float depth; // from towfish (Volts)

    private float temperature; // from towfish (Degrees C)
    private float speed; // from serial NMEA, updated on GPS update, m/s
    private float shipHeading; // from serial NMEA – Course Over Ground, Degrees
    private double shipLat; // from serial NMEA, radians
    private double shipLon; // from serial NMEA, radians
    private double fishLat; // from serial NMEA, radians
    private double fishLon; // from serial NMEA, radians
    private long tvgPage; // time-varied gain (0 = Low Gain ; 1 = High Gain)
    private long headerSize; // number of bytes in header

    private int fixTimeYear; // Time of last serial NMEA message, year
    private int fixTimeMonth; // Time of last serial NMEA message, month
    private int fixTimeDay; // Time of last serial NMEA message, day
    private int fixTimeHour; // Time of last serial NMEA message, hour
    private int fixTimeMinute; // Time of last serial NMEA message, minute
    private float fixTimeSecond; // Time of last serial NMEA message, second

    private float auxPitch; // aux data from AUV or other sensors
    private float auxRoll;
    private float auxDepth;
    private float auxAlt; // Altitude in meter.
    private float cableOut;

    private long sampleFreq; // in Hz

    private float GPSheight; // Vertical GPS Offset from datum (m) // F32;
    private long rawDataConfig; // System UUV-3500, Hydroscan: (0 = standard operation ; 1 = factory test mode)
    private long header3ExtensionSize; // Size of only this header extension. Must be equal to 256 bytes.

    /*
     * tpuSwVersion Version of the TPU s/w, 0xVVNNMMDD where VV = Major Version Number NN = Minor Version Number MM =
     * Month DD = Day
     */
    private long tpuSwVersion;

    /*
     * capabilityMask Bit mask defining various system capabilities. Bit 0 = Configured for raw data (System 5000 only)
     * Bit 1 = Configured for actuated wing Bit 2 = Configured with Sub Bottom Profiler option Bit 3 = Configured with
     * header ver. 4 Bit 4 = Configured to allow a single oversampled frequency (3000 only) Bit 5 = Configured to allow
     * dual frequency operation (3000 only) Added at tpuSwVersion 6.17. Bit 6 = 5000 (V1 or V2) system with ver. 2 Demux
     * Bit 7 = 5000 V2 towfish Added at tpuSwVersion 7.00 Bit 8 = Configured for external trigger (Slave mode) Added at
     * tpuSwVersion 8.00 Bit 9 = Configured with hull mount transducers Bit 10 = Configured to accept input from array
     * sound speed sensor. Bit 11 = Pressure sensor parameters are valid in header. Bit 12 = Configured with psig
     * pressure sensor. Otherwise, psia assumed. Bit 13 = 3900 system configured with 1dB TVG steps from - 7dB to +7dB
     * for high frequency. Otherwise, configured for 3dB steps from -21dB to +21dB. Bit 14 = Configured as UUV-3500.
     * This is a “3000-like” multi-channel single beam system. Bit 15 = System configured with 5900 gap filler sonar Bit
     * 16 = System has 1PPS trigger mode
     */
    private long capabilityMask;

    /*
     * The extra number of samples included in each data channel to account for chirp Tx waveforms. This value is zero
     * for other waveforms.
     */
    private long numSamplesExtra;
    private long postProcessVersion;

    /*
     * The type of motion sensor present in the towfish 0 = Standard TCM Compass 1 = KMS-01 Configuration 1 2 = POS MV
     * Version 3 or 4 4 = Octopus F180 8 = Teledyne TSS1 16 = Ocean Server IMU
     */
    private short motionSensorType;
    private float pingInterval; // The ping interval in seconds // F32 pingInterval
    private long sdfExtensionSize; // Size (in bytes), of the SDF extension area. If 0, no extension present.

    /*
     * The source of the header speedSound value: 0 = manual speed of sound 1 = array speed of sound sensor
     */
    private long speedSoundSource;
    private float pressureSensorMax; // maximum pressure reading of pressure sensor // F32
    private float pressureSensorVoltageMin; // minimum voltage reading from pressure sensor (Volts) //F32
    private float pressureSensorVoltageMax; // maximum voltage reading from pressure sensor (Volts) //F32
    private float temperatureAmbient; // Towfish internal temperature (Degrees C) //F32
    private long saturationDetectThreshold; // Saturation Detect Threshold
    private long sonarFreq; // The operating frequency of the Sonar (kHz)

    /**
     * @return the txWaveform
     */
    public long getTxWaveform() {
        return txWaveform;
    }

    /**
     * @param txWaveform the txWaveform to set
     */
    public void setTxWaveform(long txWaveform) {
        this.txWaveform = txWaveform;
    }

    public long getNumberBytes() {
        return numberBytes;
    }

    public void setNumberBytes(long numberBytes) {
        this.numberBytes = numberBytes;
    }

    public long getPageVersion() {
        return pageVersion;
    }

    public void setPageVersion(long pageVersion) {
        this.pageVersion = pageVersion;
    }

    public long getConfiguration() {
        return configuration;
    }

    public void setConfiguration(long configuration) {
        this.configuration = configuration;
    }

    public long getPingNumber() {
        return pingNumber;
    }

    public void setPingNumber(long pingNumber) {
        this.pingNumber = pingNumber;
    }

    public long getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(long numSamples) {
        this.numSamples = numSamples;
    }

    public long getErrorFlags() {
        return errorFlags;
    }

    public void setErrorFlags(long errorFlags) {
        this.errorFlags = errorFlags;
    }

    public long getRange() {
        return rangeMeters;
    }

    public void setRange(long range) {
        this.rangeMeters = range;
    }

    /**
     * @return the speedFish
     */
    public long getSpeedFish() {
        return speedFish;
    }

    /**
     * @param speedFish the speedFish to set
     */
    public void setSpeedFish(long speedFish) {
        this.speedFish = speedFish;
    }

    /**
     * @return the speedSound
     */
    public long getSpeedSound() {
        return speedSound;
    }

    /**
     * @param speedSound the speedSound to set
     */
    public void setSpeedSound(long speedSound) {
        this.speedSound = speedSound;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * @return the minute
     */
    public int getMinute() {
        return minute;
    }

    /**
     * @param minute the minute to set
     */
    public void setMinute(int minute) {
        this.minute = minute;
    }

    /**
     * @return the hSecond
     */
    public int gethSecond() {
        return hSecond;
    }

    /**
     * @param hSecond the hSecond to set
     */
    public void sethSecond(int hSecond) {
        this.hSecond = hSecond;
    }

    /**
     * @return the hour
     */
    public int getHour() {
        return hour;
    }

    /**
     * @param hour the hour to set
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * @return the fixTimeHour
     */
    public int getFixTimeHour() {
        return fixTimeHour;
    }

    /**
     * @param fixTimeHour the fixTimeHour to set
     */
    public void setFixTimeHour(int fixTimeHour) {
        this.fixTimeHour = fixTimeHour;
    }

    /**
     * @return the fixTimeMinute
     */
    public int getFixTimeMinute() {
        return fixTimeMinute;
    }

    /**
     * @param fixTimeMinute the fixTimeMinute to set
     */
    public void setFixTimeMinute(int fixTimeMinute) {
        this.fixTimeMinute = fixTimeMinute;
    }

    /**
     * @return the fixTimeSecond
     */
    public float getFixTimeSecond() {
        return fixTimeSecond;
    }

    /**
     * @param fixTimeSecond the fixTimeSecond to set
     */
    public void setFixTimeSecond(float fixTimeSecond) {
        this.fixTimeSecond = fixTimeSecond;
    }

    /**
     * @return the fishHeading
     */
    public float getFishHeading() {
        return fishHeading;
    }

    /**
     * @param fishHeading the fishHeading to set
     */
    public void setFishHeading(float fishHeading) {
        this.fishHeading = fishHeading;
    }

    /**
     * @return the pitch
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * @param pitch the pitch to set
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * @return the roll
     */
    public float getRoll() {
        return roll;
    }

    /**
     * @param roll the roll to set
     */
    public void setRoll(float roll) {
        this.roll = roll;
    }

    /**
     * @return the depth
     */
    public float getDepth() {
        return depth;
    }

    /**
     * @param depth the depth to set
     */
    public void setDepth(float depth) {
        this.depth = depth;
    }

    /**
     * @return the temperature
     */
    public float getTemperature() {
        return temperature;
    }

    /**
     * @param temperature the temperature to set
     */
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    /**
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * @return the shipHeading
     */
    public float getShipHeading() {
        return shipHeading;
    }

    /**
     * @param shipHeading the shipHeading to set
     */
    public void setShipHeading(float shipHeading) {
        this.shipHeading = shipHeading;
    }

    /**
     * @return the shipLat
     */
    public double getShipLat() {
        return shipLat;
    }

    /**
     * @param shipLat the shipLat to set
     */
    public void setShipLat(double shipLat) {
        this.shipLat = shipLat;
    }

    /**
     * @return the shipLon
     */
    public double getShipLon() {
        return shipLon;
    }

    /**
     * @param shipLon the shipLon to set
     */
    public void setShipLon(double shipLon) {
        this.shipLon = shipLon;
    }

    /**
     * @return the fishLon
     */
    public double getFishLon() {
        return fishLon;
    }

    /**
     * @param fishLon the fishLon to set
     */
    public void setFishLon(double fishLon) {
        this.fishLon = fishLon;
    }

    /**
     * @return the fishLat
     */
    public double getFishLat() {
        return fishLat;
    }

    /**
     * @param fishLat the fishLat to set
     */
    public void setFishLat(double fishLat) {
        this.fishLat = fishLat;
    }

    /**
     * @return the tvgPage
     */
    public long getTvgPage() {
        return tvgPage;
    }

    /**
     * @param tvgPage the tvgPage to set
     */
    public void setTvgPage(long tvgPage) {
        this.tvgPage = tvgPage;
    }

    /**
     * @return the headerSize
     */
    public long getHeaderSize() {
        return headerSize;
    }

    /**
     * @param headerSize the headerSize to set
     */
    public void setHeaderSize(long headerSize) {
        this.headerSize = headerSize;
    }

    /**
     * @return the fixTimeYear
     */
    public long getFixTimeYear() {
        return fixTimeYear;
    }

    /**
     * @param fixTimeYear the fixTimeYear to set
     */
    public void setFixTimeYear(int fixTimeYear) {
        this.fixTimeYear = fixTimeYear;
    }

    /**
     * @return the fixTimeMonth
     */
    public int getFixTimeMonth() {
        return fixTimeMonth;
    }

    /**
     * @param fixTimeMonth the fixTimeMonth to set
     */
    public void setFixTimeMonth(int fixTimeMonth) {
        this.fixTimeMonth = fixTimeMonth;
    }

    /**
     * @return the fixTimeDay
     */
    public int getFixTimeDay() {
        return fixTimeDay;
    }

    /**
     * @param fixTimeDay the fixTimeDay to set
     */
    public void setFixTimeDay(int fixTimeDay) {
        this.fixTimeDay = fixTimeDay;
    }

    /**
     * @return the auxPitch
     */
    public float getAuxPitch() {
        return auxPitch;
    }

    /**
     * @param auxPitch the auxPitch to set
     */
    public void setAuxPitch(float auxPitch) {
        this.auxPitch = auxPitch;
    }

    /**
     * @return the second
     */
    public int getSecond() {
        return second;
    }

    /**
     * @param second the second to set
     */
    public void setSecond(int second) {
        this.second = second;
    }

    /**
     * @return the auxRoll
     */
    public float getAuxRoll() {
        return auxRoll;
    }

    /**
     * @param auxRoll the auxRoll to set
     */
    public void setAuxRoll(float auxRoll) {
        this.auxRoll = auxRoll;
    }

    /**
     * @return the auxDepth
     */
    public float getAuxDepth() {
        return auxDepth;
    }

    /**
     * @param auxDepth the auxDepth to set
     */
    public void setAuxDepth(float auxDepth) {
        this.auxDepth = auxDepth;
    }

    /**
     * @return the auxAlt
     */
    public float getAuxAlt() {
        return auxAlt;
    }

    /**
     * @param auxAlt the auxAlt to set
     */
    public void setAuxAlt(float auxAlt) {
        this.auxAlt = auxAlt;
    }

    /**
     * @return the cableOut
     */
    public float getCableOut() {
        return cableOut;
    }

    /**
     * @param cableOut the cableOut to set
     */
    public void setCableOut(float cableOut) {
        this.cableOut = cableOut;
    }

    /**
     * @return the fseconds
     */
    public float getfSecond() {
        return fSecond;
    }

    /**
     * @param fseconds the fseconds to set
     */
    public void setfSecond(float fseconds) {
        this.fSecond = fseconds;
    }

    /**
     * @return the sampleFreq
     */
    public long getSampleFreq() {
        return sampleFreq;
    }

    /**
     * @param sampleFreq the sampleFreq to set
     */
    public void setSampleFreq(long sampleFreq) {
        this.sampleFreq = sampleFreq;
    }

    /**
     * @return the capabilityMask
     */
    public long getCapabilityMask() {
        return capabilityMask;
    }

    /**
     * @param capabilityMask the capabilityMask to set
     */
    public void setCapabilityMask(long capabilityMask) {
        this.capabilityMask = capabilityMask;
    }

    /**
     * @return the tpuSwVersion
     */
    public long getTPUSwVersion() {
        return tpuSwVersion;
    }

    /**
     * @param tpuSwVersion the tpuSwVersion to set
     */
    public void setTPUSwVersion(long tpuSwVersion) {
        this.tpuSwVersion = tpuSwVersion;
    }

    /**
     * @return the gPSheight
     */
    public float getGPSheight() {
        return GPSheight;
    }

    /**
     * @param gPSheight the gPSheight to set
     */
    public void setGPSheight(float gPSheight) {
        GPSheight = gPSheight;
    }

    /**
     * @return the rawDataConfig
     */
    public long getRawDataConfig() {
        return rawDataConfig;
    }

    /**
     * @param rawDataConfig the rawDataConfig to set
     */
    public void setRawDataConfig(long rawDataConfig) {
        this.rawDataConfig = rawDataConfig;
    }

    /**
     * @return the header3ExtensionSize
     */
    public long getHeader3ExtensionSize() {
        return header3ExtensionSize;
    }

    /**
     * @param header3ExtensionSize the header3ExtensionSize to set
     */
    public void setHeader3ExtensionSize(long header3ExtensionSize) {
        this.header3ExtensionSize = header3ExtensionSize;
    }

    /**
     * @return the numSamplesExtra
     */
    public long getNumSamplesExtra() {
        return numSamplesExtra;
    }

    /**
     * @param numSamplesExtra the numSamplesExtra to set
     */
    public void setNumSamplesExtra(long numSamplesExtra) {
        this.numSamplesExtra = numSamplesExtra;
    }

    /**
     * @return the postProcessVersion
     */
    public long getPostProcessVersion() {
        return postProcessVersion;
    }

    /**
     * @param postProcessVersion the postProcessVersion to set
     */
    public void setPostProcessVersion(long postProcessVersion) {
        this.postProcessVersion = postProcessVersion;
    }

    /**
     * @return the motionSensorType
     */
    public short getMotionSensorType() {
        return motionSensorType;
    }

    /**
     * @return the pingInterval
     */
    public float getPingInterval() {
        return pingInterval;
    }

    /**
     * @param pingInterval the pingInterval to set
     */
    public void setPingInterval(float pingInterval) {
        this.pingInterval = pingInterval;
    }

    /**
     * @return the sdfExtensionSize
     */
    public long getSDFExtensionSize() {
        return sdfExtensionSize;
    }

    /**
     * @param sdfExtensionSize the sdfExtensionSize to set
     */
    public void setSDFExtensionSize(long sdfExtensionSize) {
        this.sdfExtensionSize = sdfExtensionSize;
    }

    /**
     * @return the speedSoundSource
     */
    public long getSpeedSoundSource() {
        return speedSoundSource;
    }

    /**
     * @param speedSoundSource the speedSoundSource to set
     */
    public void setSpeedSoundSource(long speedSoundSource) {
        this.speedSoundSource = speedSoundSource;
    }

    /**
     * @return the pressureSensorMax
     */
    public float getPressureSensorMax() {
        return pressureSensorMax;
    }

    /**
     * @param pressureSensorMax the pressureSensorMax to set
     */ // setErrorFlags(buffer.getInt(28));
    public void setPressureSensorMax(float pressureSensorMax) {
        this.pressureSensorMax = pressureSensorMax;
    }

    /**
     * @return the pressureSensorVoltageMin
     */
    public float getPressureSensorVoltMin() {
        return pressureSensorVoltageMin;
    }

    /**
     * @param pressureSensorVoltageMin the pressureSensorVoltageMin to set
     */
    public void setPressureSensorVoltMin(float pressureSensorVoltageMin) {
        this.pressureSensorVoltageMin = pressureSensorVoltageMin;
    }

    /**
     * @return the temperatureAmbient
     */
    public float getTemperatureAmbient() {
        return temperatureAmbient;
    }

    /**
     * @param temperatureAmbient the temperatureAmbient to set
     */
    public void setTemperatureAmbient(float temperatureAmbient) {
        this.temperatureAmbient = temperatureAmbient;
    }

    /**
     * @return the pressureSensorVoltageMax
     */
    public float getPressureSensorVoltMax() {
        return pressureSensorVoltageMax;
    }

    /**
     * @param pressureSensorVoltageMax the pressureSensorVoltageMax to set
     */
    public void setPressureSensorVoltMax(float pressureSensorVoltageMax) {
        this.pressureSensorVoltageMax = pressureSensorVoltageMax;
    }

    /**
     * @return the saturationDetectThreshold
     */
    public long getSaturationDetectThreshold() {
        return saturationDetectThreshold;
    }

    /**
     * @param saturationDetectThreshold the saturationDetectThreshold to set
     */
    public void setSaturationDetectThreshold(long saturationDetectThreshold) {
        this.saturationDetectThreshold = saturationDetectThreshold;
    }

    /**
     * @return the sonarFreq
     */
    public long getSonarFreq() {
        return sonarFreq;
    }

    /**
     * @param sonarFreq the sonarFreq to set
     */
    public void setSonarFreq(long sonarFreq) {
        this.sonarFreq = sonarFreq;
    }

    public void parse(ByteBuffer buffer) {
        // The SonarPro® generated Sonar Data Files (SDF) have an “.sdf” file
        // extensions. These files are in the form; [data page][data page] … etc
        // – where each data page is the ping marker followed by the SDF data
        // page as described in section 3. The ping marker is a 32-bit value
        // that never changes and is equal to 0xFFFFFFFF (2^32-1).

        setNumberBytes(buffer.getInt(4) & 0xffffffffL);
        setPageVersion(buffer.getInt(8) & 0xffffffffL);
        setConfiguration(buffer.getInt(12) & 0xffffffffL);
        setPingNumber(buffer.getInt(16) & 0xffffffffL);
        setNumSamples(buffer.getInt(20) & 0xffffffffL);
        setRange(buffer.getInt(32) & 0xffffffffL);
        setSpeedFish(buffer.getInt(36) & 0xffffffffL);

        // TPU Time of ping
        setYear(buffer.getInt(72));
        setMonth(buffer.getInt(76));
        setDay(buffer.getInt(80));
        setHour(buffer.getInt(84));
        setMinute(buffer.getInt(88));
        setSecond(buffer.getInt(92));
        sethSecond(buffer.getInt(96));
        setfSecond(buffer.getFloat(220));

        setPitch(buffer.getFloat(116));
        setRoll(buffer.getFloat(120));
        setDepth(buffer.getFloat(124));
        setTemperature(buffer.getFloat(132));
        setShipHeading(buffer.getFloat(140));
        setShipLat(buffer.getDouble(148));
        setShipLon(buffer.getDouble(156));
        setHeaderSize(buffer.getInt(184) & 0xffffffffL);
        setAuxPitch(buffer.getFloat(200));
        setAuxRoll(buffer.getFloat(204));
        setAuxDepth(buffer.getFloat(208));
        setAuxAlt(buffer.getFloat(212));
        setSampleFreq(buffer.getInt(228) & 0xffffffffL);
        setSonarFreq(buffer.getInt(408) & 0xffffffffL);
    }

    @Override
    public String toString() {
        return "number of Bytes " + getNumberBytes() + "\npage version " + getPageVersion() + "\nconfig "
                + Long.toHexString(getConfiguration()) + "\nping number " + getPingNumber() + "\nnumber of samples "
                + getNumSamples() + "\nerror flags " + getErrorFlags() + "\nrange " + getRange() + "\nspeedFish "
                + getSpeedFish() + "\nspeedSound " + getSpeedSound() + "\ntxWaveForm "
                + Long.toHexString(getTxWaveform()) + "\nyear " + getYear() + "\nmonth " + getMonth() + "\nday "
                + getDay() + "\nhour " + getHour() + "\nminute " + getMinute() + "\nsecond " + getSecond()
                + "\nhSecond " + gethSecond() + "\nfixTimeHour " + getFixTimeHour() + "\nfixTimeMinute "
                + getFixTimeMinute() + "\nfixTimeSecond " + getFixTimeSecond() + "\nfish heading" + getFishHeading()
                + "\npitch " + getPitch() + "\nroll " + getRoll() + "\ndepth " + getDepth() + "\ntemperature "
                + getTemperature() + "\nspeed " + getSpeed() + "\nshipHeading " + getShipHeading() + "\nshipLat "
                + getShipLat() + "\nshipLon " + getShipLon() + "\nfishLat " + getFishLat() + "\nfishLon " + getFishLon()
                + "\ntvgPage " + getTvgPage() + "\nheaderSize " + getHeaderSize() + "\nfixTimeYear " + getFixTimeYear()
                + "\nfixTimeMonth " + getFixTimeMonth() + "\nfixTimeDay " + getFixTimeDay() + "\nauxPitch "
                + getAuxPitch() + "\nauxRoll " + getAuxRoll() + "\nauxDepth " + getAuxDepth() + "\nauxAlt "
                + getAuxAlt() + "\ncableOut " + getCableOut() + "\nfseconds " + getfSecond() + "\nsampleFreq "
                + getSampleFreq() + "\nrawDataConfig " + getRawDataConfig() + "\nheader3ExtensionSize "
                + getHeader3ExtensionSize() + "\ntpuSwVersion " + Long.toHexString(getTPUSwVersion())
                + "\ncapabilityMask " + Long.toHexString(getCapabilityMask()) + "\nnumSamplesExtra "
                + getNumSamplesExtra() + "\npostProcessVersion " + getPostProcessVersion() + "\nmotionSensorType "
                + getMotionSensorType() + "\npingInterval " + getPingInterval() + "\nsdfExtensionSize "
                + getSDFExtensionSize() + "\nspeedSoundSource " + getSpeedSoundSource() + "\npressureSensorMax "
                + getPressureSensorMax() + "\npressureSensorVoltageMin " + getPressureSensorVoltMin()
                + "\npressureSensorVoltageMax " + getPressureSensorVoltMax() + "\ntemperatureAmbient "
                + getTemperatureAmbient() + "\nsaturationDetectThreshold " + getSaturationDetectThreshold()
                + "\nsonarFreq " + getSonarFreq();
    }
}
