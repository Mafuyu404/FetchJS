package com.sighs.fetchjs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod.EventBusSubscriber(modid = Fetchjs.MODID)
public class test {
//    @SubscribeEvent
//    public static void jump(LivingEvent.LivingJumpEvent event) {
//        if (!event.getEntity().level().isClientSide()) return;
//        if (event.getEntity() instanceof Player player) {
//            HttpUtil.fetch("https://api.xygeng.cn/one", string -> {
//                player.displayClientMessage(Component.literal(string), true);
//            });
//            HttpUtil.download("https://raw.githubusercontent.com/Tower-of-Sighs/SmartKeyPrompts/refs/heads/master/libs/SlashBladeResharped-1.20.1-1.3.40.jar", "SlashBladeResharped-1.20.1-1.3.40.jar", n -> {
//                player.displayClientMessage(Component.literal("下载中" + Math.round(n * 100) + "%"), true);
//            });
//        }
//    }
}
