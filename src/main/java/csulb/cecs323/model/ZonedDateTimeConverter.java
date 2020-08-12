package csulb.cecs323.model;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

@Converter (autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, GregorianCalendar>
{
    @Override
    public GregorianCalendar convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : GregorianCalendar.from(zonedDateTime);
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(GregorianCalendar gregorianCalendar) {
        return gregorianCalendar == null ? null : gregorianCalendar.toZonedDateTime();
    }
}
