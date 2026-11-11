package com.erenmaral.dokun;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Oda kodunun uretilecegi IP adresinin secimi (cihazda gorulen gercek arayuz adlariyla): Wi-Fi, hotspot, mobil veri, VPN.
 * Yerel calistirma: android/ klasorunde ./gradlew testDebugUnitTest
 */
public class LanServerPluginTest {
    private static LanServerPlugin.Cand c(String name, String ip, boolean p2p) {
        return new LanServerPlugin.Cand(name, ip, p2p);
    }

    private static String best(LanServerPlugin.Cand... cs) {
        List<LanServerPlugin.Cand> l = LanServerPlugin.rank(new ArrayList<>(Arrays.asList(cs)));
        return l.isEmpty() ? "yok" : l.get(0).ip;
    }

    @Test
    public void wifiOnly() {
        assertEquals("192.168.1.23", best(c("wlan0", "192.168.1.23", false)));
    }

    @Test
    public void cellularWithPrivateAddressIsIgnored() {
        assertEquals("192.168.1.23", best(c("rmnet_data0", "10.45.2.3", true), c("wlan0", "192.168.1.23", false)));
        assertEquals("yok", best(c("rmnet_data0", "10.45.2.3", true), c("ccmni0", "10.1.1.1", false)));
    }

    @Test
    public void vpnIsIgnored() {
        assertEquals("192.168.0.5", best(c("tun0", "10.8.0.2", true), c("wlan0", "192.168.0.5", false)));
        assertEquals("192.168.0.5", best(c("vpn0", "10.9.9.9", true), c("wlan0", "192.168.0.5", false)));
    }

    @Test
    public void hotspotOwnerWins() {
        assertEquals("192.168.43.1", best(c("ap0", "192.168.43.1", false), c("rmnet_data1", "10.0.0.7", true)));
        assertEquals("192.168.20.1", best(c("swlan0", "192.168.20.1", false), c("rmnet0", "10.0.0.7", true)));
        // hotspot arayuzu wlan1 adini tasiyorsa ".1" ile biten adres onu ele verir
        assertEquals("192.168.49.1", best(c("wlan0", "192.168.1.23", false), c("wlan1", "192.168.49.1", false)));
    }

    @Test
    public void iphoneHotspotClient() {
        assertEquals("172.20.10.5", best(c("wlan0", "172.20.10.5", false)));
    }

    @Test
    public void wifiDirectAndEmptyListGiveNothing() {
        assertEquals("yok", best(c("p2p-wlan0-0", "192.168.49.1", false)));
        assertEquals("yok", best());
        assertEquals("yok", best(c("wlan0", null, false)));
    }

    @Test
    public void orderIsStable() {
        assertEquals("192.168.1.23", best(c("wlan1", "192.168.5.9", false), c("wlan0", "192.168.1.23", false)));
        assertTrue(LanServerPlugin.rank(new ArrayList<LanServerPlugin.Cand>()).isEmpty());
    }
}
