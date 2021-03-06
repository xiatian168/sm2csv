package ua.com.elius.sm2csv.record;

import java.util.ArrayList;
import java.util.List;

public class EasyBuilderRecord {
    public static final String EB_ADDRESS_TYPE_ANALOG = "%MW";
    public static final String EB_ADDRESS_TYPE_DIGITAL = "%M";
    public static final String EB_ADDRESS_TYPE_DIGITAL_IN_ANALOG = "%MW_Bit";

    private String mName;
    private String mPlcName;
    private String mAddressType;
    private String mAddress;
    private String mComment;
    private String mValueType;

    public String getName() {
        return mName;
    }
    public String getPlcName() {
        return mPlcName;
    }
    public String getAddressType() {
        return mAddressType;
    }
    public String getAddress() {
        return mAddress;
    }
    public String getComment() {
        return mComment;
    }
    public String getValueType() {
        return mValueType;
    }

    public void setName(String name) {
        this.mName = name;
    }
    public void setPlcName(String plcName) {
        this.mPlcName = plcName;
    }
    public void setAddressType(String addressType) {
        this.mAddressType = addressType;
    }
    public void setAddress(String address) {
        this.mAddress = address;
    }
    public void setComment(String comment) {
        this.mComment = comment;
    }
    public void setValueType(String valueType) {
        this.mValueType = valueType;
    }

    private EasyBuilderRecord() {}

    public static EasyBuilderRecord fromSoMachineRecord(SoMachineRecord smRec) throws UnsupportedAddressException {
        EasyBuilderRecord.Builder builder =
                new EasyBuilderRecord.Builder()
                    .name(smRec.getName());

        SoMachineRecord.Address smAddress = smRec.getAddress();
        if (smAddress != null) {
            if (smAddress.isDigital()) {
                builder.addressType(EB_ADDRESS_TYPE_DIGITAL_IN_ANALOG);
                builder.address(fromSoMachineAddress(smAddress.getNumber(), smAddress.getDigit()));
            } else if (smAddress.isAnalog()) {
                builder.addressType(EB_ADDRESS_TYPE_ANALOG);
                builder.address(fromSoMachineAddress(smAddress.getType(), smAddress.getNumber()));
            } else {
                throw new UnsupportedAddressException(smAddress.toString());
            }
        }

        builder.comment(smRec.getComment());

        return builder.build();
    }

    private static String fromSoMachineAddress(int number, int digit) {
        int newNumber = number / 2;
        int newDigit;
        if (number % 2 == 0) {
            newDigit = digit;
        } else {
            newDigit = digit + 8;
        }
        return newNumber + String.format("%02d", newDigit);
    }

    private static String fromSoMachineAddress(String type, int number) {
        int newNumber;
        switch (type) {
            case SoMachineRecord.SM_ADDRESS_TYPE_DWORD:
                newNumber = number * 2;
                break;
            default:
                newNumber = number;
        }
        return Integer.toString(newNumber);
    }

    public List<String> toList() {
        List<String> list = new ArrayList<>();
        list.add(mName);
        list.add(mPlcName);
        list.add(mAddressType);
        list.add(mAddress);
        list.add(mComment);
        list.add(mValueType);
        return list;
    }

    public boolean isAlarm() {
        return mName.startsWith("f_") || mName.startsWith("break_");
    }

    public static class Builder {
        private EasyBuilderRecord mRec = new EasyBuilderRecord();

        public Builder name(String name) {
            mRec.setName(name);
            return this;
        }
        public Builder plcName(String plcName) {
            mRec.setPlcName(plcName);
            return this;
        }
        public Builder addressType(String addressType) {
            mRec.setAddressType(addressType);
            return this;
        }
        public Builder address(String address) {
            mRec.setAddress(address);
            return this;
        }
        public Builder comment(String comment) {
            mRec.setComment(comment);
            return this;
        }
        public Builder valueType(String valueType) {
            mRec.setValueType(valueType);
            return this;
        }
        public EasyBuilderRecord build() {
            return mRec;
        }
    }

}
