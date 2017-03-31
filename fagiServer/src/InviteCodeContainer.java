import com.fagi.model.InviteCode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by costa on 31-03-2017.
 */
public class InviteCodeContainer implements Serializable {
    private final List<InviteCode> codes;

    public InviteCodeContainer(List<InviteCode> codes) {
        this.codes = codes;
    }

    public List<InviteCode> getCodes() {
        return codes;
    }

    public boolean contains(InviteCode inviteCode) {
        return codes.contains(inviteCode);
    }

    public void remove(InviteCode inviteCode) {
        codes.remove(inviteCode);
    }
}
