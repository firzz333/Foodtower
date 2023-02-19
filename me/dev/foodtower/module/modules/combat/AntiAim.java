package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.movement.Scaffold;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.value.Mode;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class AntiAim extends Module {
    float[] lastAngles;
    public static float rotationPitch;
    private boolean fake;
    private boolean fake1;
    public static byte var4;
    public static float pitchDown;
    public static float lastMeme;
    public static float reverse;
    public static float sutter;
    public static boolean NMSL;
    private static float lastP;
    Minecraft var10000;
    TimerUtil fakeJitter;
    private Mode<Enum> AAYAW;
    private Mode<Enum> AAPITCH;

    public AntiAim() {
        super("AntiAim", "大风车", new String[] { "AntiAim" }, ModuleType.Combat);
        this.fakeJitter = new TimerUtil();
        this.AAYAW = new Mode<Enum>("Yaw", "yaw", YAW.values(), YAW.FakeJitter);
        this.AAPITCH = new Mode<Enum>("Pitch", "pitch", PITCH.values(), PITCH.HalfDown);
        this.setColor(new Color(AntiAim.random.nextInt(255), AntiAim.random.nextInt(255), AntiAim.random.nextInt(255)).getRGB());
    }

    public void updateAngles(final float yaw, final float pitch) {
        if (AntiAim.mc.gameSettings.thirdPersonView != 0) {
            mc.thePlayer.rotationPitch = pitch;
            AntiAim.mc.thePlayer.rotationYawHead = yaw;
            AntiAim.mc.thePlayer.renderYawOffset = yaw;
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.rotationPitch = AntiAim.lastP;
        this.fake1 = true;
        this.lastAngles = null;
        AntiAim.rotationPitch = 0.0f;
        AntiAim.mc.thePlayer.renderYawOffset = AntiAim.mc.thePlayer.rotationYaw;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        AntiAim.lastP = mc.thePlayer.rotationPitch;
        this.fake1 = true;
        this.lastAngles = null;
        AntiAim.rotationPitch = 0.0f;
        super.onEnable();
    }

    @NMSL
    public void onEvent(final EventPreUpdate event) {
        final EventPreUpdate em = event;
        final Scaffold Scaffold = (Scaffold)Client.instance.getModuleManager().getModuleByClass(Scaffold.class);
        if ((Killaura.target == null || Aura.curTarget == null) && !Scaffold.isEnabled()) {
            if (this.lastAngles == null) {
                final float[] var10001 = new float[2];
                Minecraft var10002 = AntiAim.mc;
                var10001[0] = AntiAim.mc.thePlayer.rotationYaw;
                var10002 = AntiAim.mc;
                var10001[1] = event.getPitch();
                this.lastAngles = var10001;
            }
            this.fake = !this.fake;
            if (this.AAYAW.getValue() == YAW.Jitter) {
                AntiAim.var4 = 0;
            }
            if (this.AAYAW.getValue() == YAW.SpinFast) {
                AntiAim.var4 = 7;
            }
            if (this.AAYAW.getValue() == YAW.SpinSlow) {
                AntiAim.var4 = 8;
            }
            if (this.AAYAW.getValue() == YAW.Freestanding) {
                AntiAim.var4 = 6;
            }
            if (this.AAYAW.getValue() == YAW.Reverse) {
                AntiAim.var4 = 2;
            }
            if (this.AAYAW.getValue() == YAW.FakeJitter) {
                AntiAim.var4 = 4;
            }
            if (this.AAYAW.getValue() == YAW.Lisp) {
                AntiAim.var4 = 1;
            }
            if (this.AAYAW.getValue() == YAW.Sideways) {
                AntiAim.var4 = 3;
            }
            if (this.AAYAW.getValue() == YAW.FakeHead) {
                AntiAim.var4 = 5;
            }
            switch (AntiAim.var4) {
                case 0:
                    AntiAim.pitchDown = 0.0f;
                    em.setYaw(AntiAim.pitchDown = this.lastAngles[0] + 90.0f);
                    this.lastAngles = new float[] { AntiAim.pitchDown, this.lastAngles[1] };
                    this.updateAngles(AntiAim.pitchDown, this.lastAngles[1]);
                    this.var10000 = AntiAim.mc;
                    AntiAim.mc.thePlayer.renderYawOffset = AntiAim.pitchDown;
                    this.var10000 = AntiAim.mc;
                    AntiAim.mc.thePlayer.prevRenderYawOffset = AntiAim.pitchDown;
                    break;
                case 1:
                    AntiAim.lastMeme = this.lastAngles[0] + 150000.0f;
                    this.lastAngles = new float[] { AntiAim.lastMeme, this.lastAngles[1] };
                    em.setYaw(AntiAim.lastMeme);
                    this.updateAngles(AntiAim.lastMeme, this.lastAngles[1]);
                    break;
                case 2:
                    this.var10000 = AntiAim.mc;
                    AntiAim.reverse = AntiAim.mc.thePlayer.rotationYaw + 180.0f;
                    this.lastAngles = new float[] { AntiAim.reverse, this.lastAngles[1] };
                    em.setYaw(AntiAim.reverse);
                    this.updateAngles(AntiAim.reverse, this.lastAngles[1]);
                    break;
                case 3:
                    this.var10000 = AntiAim.mc;
                    AntiAim.sutter = AntiAim.mc.thePlayer.rotationYaw - 90.0f;
                    this.lastAngles = new float[] { AntiAim.sutter, this.lastAngles[1] };
                    em.setYaw(AntiAim.sutter);
                    this.updateAngles(AntiAim.sutter, this.lastAngles[1]);
                    break;
                case 4: {
                    if (this.fakeJitter.delay(350.0F)) {
                        this.fake1 = !this.fake1;
                        this.fakeJitter.reset();
                    }
                    this.var10000 = AntiAim.mc;
                    final float yawRight = AntiAim.mc.thePlayer.rotationYaw + (this.fake1 ? 90 : -90);
                    this.lastAngles = new float[] { yawRight, this.lastAngles[1] };
                    em.setYaw(yawRight);
                    this.updateAngles(yawRight, this.lastAngles[1]);
                    break;
                }
                case 5: {
                    if (this.fakeJitter.delay(1100.0F)) {
                        this.fake1 = !this.fake1;
                        this.fakeJitter.reset();
                    }
                    this.var10000 = AntiAim.mc;
                    final float yawFakeHead = AntiAim.mc.thePlayer.rotationYaw + (this.fake1 ? 90 : -90);
                    if (this.fake1) {
                        this.fake1 = false;
                    }
                    this.lastAngles = new float[] { yawFakeHead, this.lastAngles[1] };
                    em.setYaw(yawFakeHead);
                    this.updateAngles(yawFakeHead, this.lastAngles[1]);
                    break;
                }
                case 6: {
                    this.var10000 = AntiAim.mc;
                    final float freestandHead = (float)(AntiAim.mc.thePlayer.rotationYaw + 5.0f + Math.random() * 175.0);
                    this.lastAngles = new float[] { freestandHead, this.lastAngles[1] };
                    em.setYaw(freestandHead);
                    this.updateAngles(freestandHead, this.lastAngles[1]);
                    break;
                }
                case 7: {
                    final float yawSpinFast = this.lastAngles[0] + 45.0f;
                    this.lastAngles = new float[] { yawSpinFast, this.lastAngles[1] };
                    em.setYaw(yawSpinFast);
                    this.updateAngles(yawSpinFast, this.lastAngles[1]);
                    break;
                }
                case 8: {
                    final float yawSpinSlow = this.lastAngles[0] + 10.0f;
                    this.lastAngles = new float[] { yawSpinSlow, this.lastAngles[1] };
                    em.setYaw(yawSpinSlow);
                    this.updateAngles(yawSpinSlow, this.lastAngles[1]);
                    break;
                }
            }
            if (this.AAPITCH.getValue() == PITCH.Normal) {
                AntiAim.var4 = 2;
            }
            if (this.AAPITCH.getValue() == PITCH.Reverse) {
                AntiAim.var4 = 3;
            }
            if (this.AAPITCH.getValue() == PITCH.Stutter) {
                AntiAim.var4 = 4;
            }
            if (this.AAPITCH.getValue() == PITCH.Up) {
                AntiAim.var4 = 5;
            }
            if (this.AAPITCH.getValue() == PITCH.Meme) {
                AntiAim.var4 = 1;
            }
            if (this.AAPITCH.getValue() == PITCH.Zero) {
                AntiAim.var4 = 6;
            }
            if (this.AAPITCH.getValue() == PITCH.HalfDown) {
                AntiAim.var4 = 0;
            }
            switch (AntiAim.var4) {
                case 0:
                    AntiAim.pitchDown = 90.0f;
                    this.lastAngles = new float[] { this.lastAngles[0], AntiAim.pitchDown };
                    em.setPitch(AntiAim.pitchDown);
                    this.updateAngles(this.lastAngles[0], AntiAim.pitchDown);
                    break;
                case 1:
                    AntiAim.lastMeme = this.lastAngles[1];
                    AntiAim.lastMeme += 10.0f;
                    if (AntiAim.lastMeme > 90.0f) {
                        AntiAim.lastMeme = -90.0f;
                    }
                    this.lastAngles = new float[] { this.lastAngles[0], AntiAim.lastMeme };
                    em.setPitch(AntiAim.lastMeme);
                    this.updateAngles(this.lastAngles[0], AntiAim.lastMeme);
                    break;
                case 2:
                    this.updateAngles(this.lastAngles[0], AntiAim.mc.thePlayer.rotationPitch);
                    break;
                case 3:
                    this.var10000 = AntiAim.mc;
                    AntiAim.reverse = AntiAim.mc.thePlayer.rotationPitch + 180.0f;
                    this.lastAngles = new float[] { this.lastAngles[0], AntiAim.reverse };
                    em.setPitch(AntiAim.reverse);
                    this.updateAngles(this.lastAngles[0], AntiAim.reverse);
                    break;
                case 4:
                    if (this.fake) {
                        em.setPitch(AntiAim.sutter = 90.0f);
                    }
                    else {
                        em.setPitch(AntiAim.sutter = -45.0f);
                    }
                    this.lastAngles = new float[] { this.lastAngles[0], AntiAim.sutter };
                    this.updateAngles(this.lastAngles[0], AntiAim.sutter);
                    break;
                case 5:
                    this.lastAngles = new float[] { this.lastAngles[0], -90.0f };
                    em.setPitch(-90.0f);
                    this.updateAngles(this.lastAngles[0], -90.0f);
                    break;
                case 6:
                    this.lastAngles = new float[] { this.lastAngles[0], -179.0f };
                    em.setPitch(-180.0f);
                    this.updateAngles(this.lastAngles[0], -179.0f);
                    break;
            }
        }
    }

    static {
        AntiAim.var4 = -1;
        AntiAim.NMSL = false;
    }

    enum YAW
    {
        Reverse,
        Jitter,
        Lisp,
        SpinSlow,
        SpinFast,
        Sideways,
        FakeJitter,
        FakeHead,
        Freestanding;

        private static final /* synthetic */ YAW[] $VALUES;

        static {
            $VALUES = new YAW[] { YAW.Reverse, YAW.Jitter, YAW.Lisp, YAW.SpinSlow, YAW.SpinFast, YAW.Sideways, YAW.FakeJitter, YAW.FakeHead, YAW.Freestanding };
        }
    }

    enum PITCH
    {
        Normal,
        HalfDown,
        Zero,
        Up,
        Stutter,
        Reverse,
        Meme;

        private static final PITCH[] $VALUES;

        static {
            $VALUES = new PITCH[] { PITCH.Normal, PITCH.HalfDown, PITCH.Zero, PITCH.Up, PITCH.Stutter, PITCH.Reverse, PITCH.Meme };
        }
    }
}
