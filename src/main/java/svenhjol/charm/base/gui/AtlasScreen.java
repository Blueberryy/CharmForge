package svenhjol.charm.base.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import svenhjol.charm.Charm;
import svenhjol.charm.container.AtlasContainer;
import svenhjol.charm.container.AtlasInventory;
import svenhjol.charm.message.ServerTransferStackFromAtlas;

import java.util.List;
import java.util.Optional;

/**
 * @author Lukas
 * @since 28.12.2020
 */
public class AtlasScreen extends ContainerScreen<AtlasContainer> {
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Charm.MOD_ID, "textures/gui/atlas_container.png");
    private static final RenderType MAP_BACKGROUND = RenderType.getText(new ResourceLocation("textures/map/map_background.png"));
    private MapGui mapGui;

    public AtlasScreen(AtlasContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.passEvents = true;
        this.xSize = 175;
        this.ySize = 171;
        NonNullList<AtlasInventory.MapInfo> mapInfos = screenContainer.getAtlasInventory().getMapInfos();
        mapGui = mapInfos.size() > 1 ? getWorldMap() : getSingleMap(mapInfos.isEmpty() ? null : mapInfos.get(0));
    }

    private WorldMap getWorldMap() {
        return new WorldMap(80, 16, 54);
    }

    private SingleMap getSingleMap(AtlasInventory.MapInfo mapInfo) {
        return new SingleMap(80, 16, 54, mapInfo);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        mapGui.render(matrices);
        renderHoveredTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
        this.font.func_238422_b_(matrices, this.title.func_241878_f(), 8.0F, 6.0F, 4210752);
        this.font.func_238422_b_(matrices, this.playerInventory.getDisplayName().func_241878_f(), 8.0F, (float) ySize - 97, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (minecraft != null) {
            minecraft.getTextureManager().bindTexture(CONTAINER_BACKGROUND);

            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            blit(matrices, x, y, 0, 0, xSize, ySize);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mapGui.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private interface MapGui {
        void render(MatrixStack matrices);

        boolean mouseClicked(double mouseX, double mouseY, int button);
    }

    private class WorldMap implements MapGui {
        private final int left;
        private final int top;
        private final int size;

        private WorldMap(int left, int top, int size) {
            this.left = left;
            this.top = top;
            this.size = size;
        }

        @Override
        public void render(MatrixStack matrices) {
            int x = this.left + guiLeft;
            int y = this.top + guiTop;
            final Minecraft mc = Minecraft.getInstance();
            final World world = mc.world;
            if (world == null) {
                return;
            }
            int light = 240;
            float baseScale = (float) size / 128;
            List<AtlasInventory.MapInfo> mapInfos = container.getAtlasInventory().getMapInfos();
            if (mapInfos.isEmpty()) {
                return;
            }
            int minX = mapInfos.stream().mapToInt(it -> Math.floorDiv(it.x, it.radius * 2)).min().orElseThrow(IllegalStateException::new);
            int maxX = mapInfos.stream().mapToInt(it -> Math.floorDiv(it.x, it.radius * 2)).max().orElseThrow(IllegalStateException::new);
            int minY = mapInfos.stream().mapToInt(it -> Math.floorDiv(it.z, it.radius * 2)).min().orElseThrow(IllegalStateException::new);
            int maxY = mapInfos.stream().mapToInt(it -> Math.floorDiv(it.z, it.radius * 2)).max().orElseThrow(IllegalStateException::new);
            int maxMapDistance = Math.max(maxX + 1 - minX, maxY + 1 - minY);
            float mapSize = (float) size / maxMapDistance;
            matrices.push();
            matrices.translate(x, y, 0);
            matrices.scale(baseScale, baseScale, 1);
            IRenderTypeBuffer.Impl bufferSource = mc.getRenderTypeBuffers().getBufferSource();
            final IVertexBuilder background = bufferSource.getBuffer(MAP_BACKGROUND);
            Matrix4f matrix4f = matrices.getLast().getMatrix();
            background.pos(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).tex(0.0F, 1.0F).lightmap(light).endVertex();
            background.pos(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).tex(1.0F, 1.0F).lightmap(light).endVertex();
            background.pos(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).tex(1.0F, 0.0F).lightmap(light).endVertex();
            background.pos(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).tex(0.0F, 0.0F).lightmap(light).endVertex();
            matrices.pop();
            float mapScale = baseScale / maxMapDistance;
            for (AtlasInventory.MapInfo mapInfo : mapInfos) {
                matrices.push();
                matrices.translate(x + mapSize * (Math.floorDiv(mapInfo.x, mapInfo.radius * 2) - minX), y + mapSize * (Math.floorDiv(mapInfo.z, mapInfo.radius * 2) - minY), 1.0);
                matrices.scale(mapScale, mapScale, 1);
                mc.gameRenderer.getMapItemRenderer().renderMap(matrices, bufferSource, world.getMapData(FilledMapItem.getMapName(mapInfo.id)), false, light);
                matrices.pop();
            }
            drawLines(matrices, x, y, maxMapDistance);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double normX = normalizeForMapArea(left + guiLeft, mouseX);
            double normY = normalizeForMapArea(top + guiTop, mouseY);
            if (button == 0 && 0 <= normX && normX < 1 && 0 <= normY && normY < 1) {
                List<AtlasInventory.MapInfo> mapInfos = container.getAtlasInventory().getMapInfos();
                if (!mapInfos.isEmpty()) {
                    int minX = mapInfos.stream().mapToInt(it -> Math.floorDiv(it.x, it.radius * 2)).min().orElseThrow(IllegalStateException::new);
                    int maxX = mapInfos.stream().mapToInt(it -> Math.floorDiv(it.x, it.radius * 2)).max().orElseThrow(IllegalStateException::new);
                    int minY = mapInfos.stream().mapToInt(it -> Math.floorDiv(it.z, it.radius * 2)).min().orElseThrow(IllegalStateException::new);
                    int maxY = mapInfos.stream().mapToInt(it -> Math.floorDiv(it.z, it.radius * 2)).max().orElseThrow(IllegalStateException::new);
                    int maxMapDistance = Math.max(maxX + 1 - minX, maxY + 1 - minY);
                    int x = (int) (normX * maxMapDistance) + minX;
                    int y = (int) (normY * maxMapDistance) + minY;
                    Optional<AtlasInventory.MapInfo> mapInfo =
                            mapInfos.stream().filter(it -> x == Math.floorDiv(it.x, it.radius * 2) && y == Math.floorDiv(it.z, it.radius * 2)).findAny();
                    if (mapInfo.isPresent()) {
                        Charm.LOG.debug("Clicked on map #" + mapInfo.get().id);
                        if (isShiftClick()) {
                            Charm.PACKET_HANDLER.sendToServer(
                                    new ServerTransferStackFromAtlas(playerInventory.getSlotFor(container.getAtlasInventory().getAtlasItem()), mapInfo.get().slot));
                            mapInfos.remove(mapInfo.get());
                        } else {
                            mapGui = getSingleMap(mapInfo.get());
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean isShiftClick() {
            return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) ||
                    InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344);
        }

        private double normalizeForMapArea(double base, double val) {
            return (val - base) / size;
        }

        private void drawLines(MatrixStack matrices, int x, int y, int maxMapDistance) {
            matrices.push();
            matrices.translate(x, y, 2);
            //scale to half size for finer lines
            matrices.scale(0.5f, 0.5f, 1);
            for (int xLine = 1; xLine < maxMapDistance; ++xLine) {
                vLine(matrices, 2 * xLine * size / maxMapDistance, 0, size * 2, -1);
            }
            for (int yLine = 1; yLine < maxMapDistance; ++yLine) {
                hLine(matrices, 0, size * 2, 2 * yLine * size / maxMapDistance , -1);
            }
            matrices.pop();
        }
    }

    private class SingleMap implements MapGui {
        private final int left;
        private final int top;
        private final int size;
        private final AtlasInventory.MapInfo mapInfo;

        public SingleMap(int left, int top, int size, AtlasInventory.MapInfo mapInfo) {
            this.left = left;
            this.top = top;
            this.size = size;
            this.mapInfo = mapInfo;
        }

        @Override
        public void render(MatrixStack matrices) {

        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }
    }
}
