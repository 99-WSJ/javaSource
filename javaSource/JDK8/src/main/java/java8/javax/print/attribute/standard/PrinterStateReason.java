/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package java8.javax.print.attribute.standard;

import javax.print.attribute.EnumSyntax;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.Severity;

/**
 * Class PrinterStateReason is a printing attribute class, an enumeration,
 * that provides additional information about the printer's current state,
 * i.e., information that augments the value of the printer's
 * {@link PrinterState PrinterState} attribute.
 * Class PrinterStateReason defines standard printer
 * state reason values. A Print Service implementation only needs to report
 * those printer state reasons which are appropriate for the particular
 * implementation; it does not have to report every defined printer state
 * reason.
 * <P>
 * Instances of PrinterStateReason do not appear in a Print Service's
 * attribute set directly.
 * Rather, a {@link PrinterStateReasons PrinterStateReasons}
 * attribute appears in the Print Service's attribute set. The {@link
 * PrinterStateReasons PrinterStateReasons} attribute contains zero, one, or
 * more than one PrinterStateReason objects which pertain to the
 * Print Service's status, and each PrinterStateReason object is
 * associated with a {@link Severity Severity} level of REPORT (least severe),
 * WARNING, or ERROR (most severe). The printer adds a PrinterStateReason
 * object to the Print Service's
 * {@link PrinterStateReasons PrinterStateReasons} attribute when the
 * corresponding condition becomes true of the printer, and the printer
 * removes the PrinterStateReason object again when the corresponding
 * condition becomes false, regardless of whether the Print Service's overall
 * {@link PrinterState PrinterState} also changed.
 * <P>
 * <B>IPP Compatibility:</B>
 * The string values returned by each individual {@link javax.print.attribute.standard.PrinterStateReason} and
 * associated {@link Severity} object's <CODE>toString()</CODE>
 * methods, concatenated together with a hyphen (<CODE>"-"</CODE>) in
 * between, gives the IPP keyword value for a {@link PrinterStateReasons}.
 * The category name returned by <CODE>getName()</CODE> gives the IPP
 * attribute name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public class PrinterStateReason extends EnumSyntax implements Attribute {

    private static final long serialVersionUID = -1623720656201472593L;

    /**
     * The printer has detected an error other than ones listed below.
     */
    public static final javax.print.attribute.standard.PrinterStateReason OTHER = new javax.print.attribute.standard.PrinterStateReason(0);

    /**
     * A tray has run out of media.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        MEDIA_NEEDED = new javax.print.attribute.standard.PrinterStateReason(1);

    /**
     * The device has a media jam.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        MEDIA_JAM = new javax.print.attribute.standard.PrinterStateReason(2);

    /**
     * Someone has paused the printer, but the device(s) are taking an
     * appreciable time to stop. Later, when all output has stopped,
     * the {@link  PrinterState PrinterState} becomes STOPPED,
     * and the PAUSED value replaces
     * the MOVING_TO_PAUSED value in the {@link PrinterStateReasons
     * PrinterStateReasons} attribute. This value must be supported if the
     * printer can be paused and the implementation takes significant time to
     * pause a device in certain circumstances.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        MOVING_TO_PAUSED = new javax.print.attribute.standard.PrinterStateReason(3);

    /**
     * Someone has paused the printer and the printer's {@link PrinterState
     * PrinterState} is STOPPED. In this state, a printer must not produce
     * printed output, but it must perform other operations requested by a
     * client. If a printer had been printing a job when the printer was
     * paused,
     * the Printer must resume printing that job when the printer is no longer
     * paused and leave no evidence in the printed output of such a pause.
     * This value must be supported if the printer can be paused.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        PAUSED = new javax.print.attribute.standard.PrinterStateReason(4);

    /**
     * Someone has removed a printer from service, and the device may be
     * powered down or physically removed.
     * In this state, a printer must not produce
     * printed output, and unless the printer is realized by a print server
     * that is still active, the printer must perform no other operations
     * requested by a client.
     * If a printer had been printing a job when it was shut down,
     * the printer need not resume printing that job when the printer is no
     * longer shut down. If the printer resumes printing such a job, it may
     * leave evidence in the printed output of such a shutdown, e.g. the part
     * printed before the shutdown may be printed a second time after the
     * shutdown.
         */
    public static final javax.print.attribute.standard.PrinterStateReason
        SHUTDOWN = new javax.print.attribute.standard.PrinterStateReason(5);

    /**
     * The printer has scheduled a job on the output device and is in the
     * process of connecting to a shared network output device (and might not
     * be able to actually start printing the job for an arbitrarily long time
     * depending on the usage of the output device by other servers on the
     * network).
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        CONNECTING_TO_DEVICE = new javax.print.attribute.standard.PrinterStateReason(6);

    /**
     * The server was able to connect to the output device (or is always
     * connected), but was unable to get a response from the output device.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        TIMED_OUT = new javax.print.attribute.standard.PrinterStateReason(7);

    /**
     * The printer is in the process of stopping the device and will be
     * stopped in a while.
     * When the device is stopped, the printer will change the
     * {@link PrinterState PrinterState} to STOPPED. The STOPPING reason is
     * never an error, even for a printer with a single output device. When an
     * output device ceases accepting jobs, the printer's {@link
     * PrinterStateReasons PrinterStateReasons} will have this reason while
     * the output device completes printing.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        STOPPING = new javax.print.attribute.standard.PrinterStateReason(8);

    /**
     * When a printer controls more than one output device, this reason
     * indicates that one or more output devices are stopped. If the reason's
     * severity is a report, fewer than half of the output devices are
     * stopped.
     * If the reason's severity is a warning, half or more but fewer than
     * all of the output devices are stopped.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        STOPPED_PARTLY = new javax.print.attribute.standard.PrinterStateReason(9);

    /**
     * The device is low on toner.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        TONER_LOW = new javax.print.attribute.standard.PrinterStateReason(10);

    /**
     * The device is out of toner.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        TONER_EMPTY = new javax.print.attribute.standard.PrinterStateReason(11);

    /**
     * The limit of persistent storage allocated for spooling has been
     * reached.
     * The printer is temporarily unable to accept more jobs. The printer will
     * remove this reason when it is able to accept more jobs.
     * This value should  be used by a non-spooling printer that only
     * accepts one or a small number
     * jobs at a time or a spooling printer that has filled the spool space.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        SPOOL_AREA_FULL = new javax.print.attribute.standard.PrinterStateReason(12);

    /**
     * One or more covers on the device are open.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        COVER_OPEN = new javax.print.attribute.standard.PrinterStateReason(13);

    /**
     * One or more interlock devices on the printer are unlocked.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        INTERLOCK_OPEN = new javax.print.attribute.standard.PrinterStateReason(14);

    /**
     * One or more doors on the device are open.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        DOOR_OPEN = new javax.print.attribute.standard.PrinterStateReason(15);

    /**
     * One or more input trays are not in the device.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        INPUT_TRAY_MISSING = new javax.print.attribute.standard.PrinterStateReason(16);

    /**
     * At least one input tray is low on media.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        MEDIA_LOW = new javax.print.attribute.standard.PrinterStateReason(17);

    /**
     * At least one input tray is empty.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        MEDIA_EMPTY = new javax.print.attribute.standard.PrinterStateReason(18);

    /**
     * One or more output trays are not in the device.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        OUTPUT_TRAY_MISSING = new javax.print.attribute.standard.PrinterStateReason(19);

    /**
     * One or more output areas are almost full
     * (e.g. tray, stacker, collator).
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        OUTPUT_AREA_ALMOST_FULL = new javax.print.attribute.standard.PrinterStateReason(20);

    /**
     * One or more output areas are full (e.g. tray, stacker, collator).
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        OUTPUT_AREA_FULL = new javax.print.attribute.standard.PrinterStateReason(21);

    /**
     * The device is low on at least one marker supply (e.g. toner, ink,
     * ribbon).
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        MARKER_SUPPLY_LOW = new javax.print.attribute.standard.PrinterStateReason(22);

    /**
     * The device is out of at least one marker supply (e.g. toner, ink,
     * ribbon).
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        MARKER_SUPPLY_EMPTY = new javax.print.attribute.standard.PrinterStateReason(23);

    /**
     * The device marker supply waste receptacle is almost full.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        MARKER_WASTE_ALMOST_FULL = new javax.print.attribute.standard.PrinterStateReason(24);

    /**
     * The device marker supply waste receptacle is full.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        MARKER_WASTE_FULL = new javax.print.attribute.standard.PrinterStateReason(25);

    /**
     * The fuser temperature is above normal.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        FUSER_OVER_TEMP = new javax.print.attribute.standard.PrinterStateReason(26);

    /**
     * The fuser temperature is below normal.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        FUSER_UNDER_TEMP = new javax.print.attribute.standard.PrinterStateReason(27);

    /**
     * The optical photo conductor is near end of life.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        OPC_NEAR_EOL = new javax.print.attribute.standard.PrinterStateReason(28);

    /**
     * The optical photo conductor is no longer functioning.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        OPC_LIFE_OVER = new javax.print.attribute.standard.PrinterStateReason(29);

    /**
     * The device is low on developer.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        DEVELOPER_LOW = new javax.print.attribute.standard.PrinterStateReason(30);

    /**
     * The device is out of developer.
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        DEVELOPER_EMPTY = new javax.print.attribute.standard.PrinterStateReason(31);

    /**
     * An interpreter resource is unavailable (e.g., font, form).
     */
    public static final javax.print.attribute.standard.PrinterStateReason
        INTERPRETER_RESOURCE_UNAVAILABLE = new javax.print.attribute.standard.PrinterStateReason(32);

    /**
     * Construct a new printer state reason enumeration value with
     * the given integer value.
     *
     * @param  value  Integer value.
     */
    protected PrinterStateReason(int value) {
        super (value);
    }

    private static final String[] myStringTable = {
        "other",
        "media-needed",
        "media-jam",
        "moving-to-paused",
        "paused",
        "shutdown",
        "connecting-to-device",
        "timed-out",
        "stopping",
        "stopped-partly",
        "toner-low",
        "toner-empty",
        "spool-area-full",
        "cover-open",
        "interlock-open",
        "door-open",
        "input-tray-missing",
        "media-low",
        "media-empty",
        "output-tray-missing",
        "output-area-almost-full",
        "output-area-full",
        "marker-supply-low",
        "marker-supply-empty",
        "marker-waste-almost-full",
        "marker-waste-full",
        "fuser-over-temp",
        "fuser-under-temp",
        "opc-near-eol",
        "opc-life-over",
        "developer-low",
        "developer-empty",
        "interpreter-resource-unavailable"
    };

    private static final javax.print.attribute.standard.PrinterStateReason[] myEnumValueTable = {
        OTHER,
        MEDIA_NEEDED,
        MEDIA_JAM,
        MOVING_TO_PAUSED,
        PAUSED,
        SHUTDOWN,
        CONNECTING_TO_DEVICE,
        TIMED_OUT,
        STOPPING,
        STOPPED_PARTLY,
        TONER_LOW,
        TONER_EMPTY,
        SPOOL_AREA_FULL,
        COVER_OPEN,
        INTERLOCK_OPEN,
        DOOR_OPEN,
        INPUT_TRAY_MISSING,
        MEDIA_LOW,
        MEDIA_EMPTY,
        OUTPUT_TRAY_MISSING,
        OUTPUT_AREA_ALMOST_FULL,
        OUTPUT_AREA_FULL,
        MARKER_SUPPLY_LOW,
        MARKER_SUPPLY_EMPTY,
        MARKER_WASTE_ALMOST_FULL,
        MARKER_WASTE_FULL,
        FUSER_OVER_TEMP,
        FUSER_UNDER_TEMP,
        OPC_NEAR_EOL,
        OPC_LIFE_OVER,
        DEVELOPER_LOW,
        DEVELOPER_EMPTY,
        INTERPRETER_RESOURCE_UNAVAILABLE
    };

    /**
     * Returns the string table for class PrinterStateReason.
     */
    protected String[] getStringTable() {
        return (String[])myStringTable.clone();
    }

    /**
     * Returns the enumeration value table for class PrinterStateReason.
     */
    protected EnumSyntax[] getEnumValueTable() {
        return (EnumSyntax[])myEnumValueTable.clone();
    }


    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class PrinterStateReason and any vendor-defined subclasses, the
     * category is class PrinterStateReason itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.PrinterStateReason.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class PrinterStateReason and any vendor-defined subclasses, the
     * category name is <CODE>"printer-state-reason"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "printer-state-reason";
    }

}
