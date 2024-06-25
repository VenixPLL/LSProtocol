import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lightsolutions.LightProtocol;

public class ProtocolBuilderTest {

    @BeforeClass
    public static void initClass(){
        new LightProtocol();
    }

    @After
    public void cleanup(){
        LightProtocol.getInstance().getPacketRegistry().invalidate();
    }

    @Test
    public void buildDefault(){
        var protocol = LightProtocol.builder().build();

        assert protocol.getProtocolProperty().getProperty("transport") == LightProtocol.TransportType.LEGACY : "Transport type set to native by default";
        assert !(boolean)protocol.getProtocolProperty().getProperty("force-raw") : "force-raw set to true by default";
        assert (boolean)protocol.getProtocolProperty().getProperty("read-raw")  : "read-raw set to true by default";
    }

    @Test
    public void buildWithProperty(){
        var protocol = LightProtocol.builder()
                .withProperty("read-raw",false)
                .build();
        assert !(boolean)protocol.getProtocolProperty().getProperty("read-raw")  : "read-raw set to true by default";
    }

    @Test
    public void buildWithTransport(){
        var protocol = LightProtocol.builder()
                .transport(LightProtocol.TransportType.NATIVE)
                .build();
        assert protocol.getProtocolProperty().getProperty("transport") == LightProtocol.TransportType.NATIVE : "Transport type set to legacy by default";
    }

    @Test
    public void buildCombined(){
        var protocol = LightProtocol.builder()
                .withProperty("force-raw",true)
                .transport(LightProtocol.TransportType.NATIVE)
                .build();
        assert (boolean)protocol.getProtocolProperty().getProperty("force-raw")  : "force-raw set to false by default";
        assert protocol.getProtocolProperty().getProperty("transport") == LightProtocol.TransportType.NATIVE : "Transport type set to legacy by default";
    }

}
