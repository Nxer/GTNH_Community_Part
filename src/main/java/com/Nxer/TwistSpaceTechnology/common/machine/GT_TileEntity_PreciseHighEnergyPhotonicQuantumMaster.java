package com.Nxer.TwistSpaceTechnology.common.machine;

import static com.Nxer.TwistSpaceTechnology.common.block.BasicBlocks.PhotonControllerUpgrade;
import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.Mode_Default_PreciseHighEnergyPhotonicQuantumMaster;
import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.Parallel_LaserEngraverMode_PreciseHighEnergyPhotonicQuantumMaster;
import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.Parallel_PhCMode_PreciseHighEnergyPhotonicQuantumMaster;
import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.SpeedUpMultiplier_LaserEngraverMode_PreciseHighEnergyPhotonicQuantumMaster;
import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.SpeedUpMultiplier_PhCMode_PreciseHighEnergyPhotonicQuantumMaster;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PROCESSING_ARRAY;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PROCESSING_ARRAY_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PROCESSING_ARRAY_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PROCESSING_ARRAY_GLOW;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.Nxer.TwistSpaceTechnology.common.block.blockClass.Casings.PhotonControllerUpgradeCasing;
import com.Nxer.TwistSpaceTechnology.common.machine.multiMachineClasses.GTCM_MultiMachineBase;
import com.Nxer.TwistSpaceTechnology.common.machine.multiMachineClasses.processingLogics.GTCM_ProcessingLogic;
import com.Nxer.TwistSpaceTechnology.common.recipeMap.GTCMRecipe;
import com.Nxer.TwistSpaceTechnology.util.TextLocalization;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster
    extends GTCM_MultiMachineBase<GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster> {

    // region ClassConstructors
    public GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster(String aName) {
        super(aName);
    }

    // endregion

    // region Member Variables

    private boolean mode = Mode_Default_PreciseHighEnergyPhotonicQuantumMaster;
    private boolean enablePerfectOverclockSignal = false;
    private int totalSpeedIncrement = 0;
    private int recipeTier = 0;

    public int getTotalSpeedIncrement() {
        return this.totalSpeedIncrement;
    }

    // endregion

    // region Structure

    private static final String STRUCTURE_PIECE_MAIN = "main";

    // spotless:off
    private static final String[][] shape = new String[][] {
        { "               ", "DDDDDDDDDDDDDDD", "DDDDDDDDDDDDDDD", "               ", "               ", "               ", "DDDDDDDDDDDDDDD", "DDDDDDDDDDDDDDD", "     DDDDD     " },
        { "     DDDDD     ", "D   DCCCCCD   D", "DEEEEEEEEEEEEED", "DXXXXXXXXXXXXXD", "DXXXXXXXXXXXXXD", "DXXXXXXXXXXXXXD", "DEEEEEEEEEEEEED", "DDDDDCCCCCDDDDD", "     DAAAD     " },
        { "     DMMMD     ", "DDDDDCCCCCDDDDD", "DBBBBBBBBBBBBBD", " I           I ", " I           I ", " I           I ", "DBBBBBBBBBBBBBD", "DDDDDCCCCCDDDDD", "DDDDDDAAADDDDDD" },
        { "     DM~MD     ", "DDDDDCCCCCDDDDD", "DCCCCCCCCCCCCCD", " I           I ", " I           I ", " I           I ", "DCCCCCCCCCCCCCD", "DDDDDCCCCCDDDDD", "DAAAAAAAAAAAAAD" },
        { "     DMMMD     ", "DDDDDCCCCCDDDDD", "DBBBBBBBBBBBBBD", " I           I ", " I           I ", " I           I ", "DBBBBBBBBBBBBBD", "DDDDDCCCCCDDDDD", "DDDDDDAAADDDDDD" },
        { "     DDDDD     ", "D   DCCCCCD   D", "DEEEEEEEEEEEEED", "DXXXXXXXXXXXXXD", "DXXXXXXXXXXXXXD", "DXXXXXXXXXXXXXD", "DEEEEEEEEEEEEED", "DDDDDCCCCCDDDDD", "     DAAAD     " },
        { "               ", "DDDDDDDDDDDDDDD", "DDDDDDDDDDDDDDD", "               ", "               ", "               ", "DDDDDDDDDDDDDDD", "DDDDDDDDDDDDDDD", "     DDDDD     " } };

    // spotless:on
    private static final int horizontalOffSet = 7;
    private static final int verticalOffSet = 3;
    private static final int depthOffSet = 0;
    private static IStructureDefinition<GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster> STRUCTURE_DEFINITION = null;

    /**
     * Due to limitation of Java type system, you might need to do an unchecked cast. HOWEVER, the returned
     * IStructureDefinition is expected to be evaluated against current instance only, and should not be used against
     * other instances, even for those of the same class.
     */
    @Override
    public IStructureDefinition<GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('B', ofBlock(GregTechAPI.sBlockCasings2, 5))
                .addElement('C', ofBlock(GregTechAPI.sBlockCasings2, 9))
                .addElement('D', ofBlock(GregTechAPI.sBlockCasings8, 7))
                .addElement('E', ofBlock(GregTechAPI.sBlockCasings9, 1))
                .addElement(
                    'M',
                    GTStructureUtility.buildHatchAdder(GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster.class)
                        .atLeast(Maintenance)
                        .dot(1)
                        .casingIndex(183)
                        .buildAndChain(GregTechAPI.sBlockCasings8, 7))
                .addElement(
                    'I',
                    GTStructureUtility.buildHatchAdder(GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster.class)
                        .atLeast(InputBus, InputHatch, OutputBus, OutputHatch)
                        .dot(2)
                        .casingIndex(183)
                        .buildAndChain(GregTechAPI.sBlockCasings8, 7))
                .addElement(
                    'X',
                    GTStructureUtility.buildHatchAdder(GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster.class)
                        .atLeast(Energy.or(ExoticEnergy))
                        .dot(3)
                        .casingIndex(1024)
                        .buildAndChain(sBlockCasingsTT, 0))
                .addElement('A', ofChain(onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[0];
                    x.recipeTier = Math.max(recipeTier, 1);
                }, ofBlock(PhotonControllerUpgrade, 0)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[1];
                    x.recipeTier = Math.max(recipeTier, 2);
                }, ofBlock(PhotonControllerUpgrade, 1)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[2];
                    x.recipeTier = Math.max(recipeTier, 3);
                }, ofBlock(PhotonControllerUpgrade, 2)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[3];
                    x.recipeTier = Math.max(recipeTier, 4);
                }, ofBlock(PhotonControllerUpgrade, 3)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[4];
                    x.recipeTier = Math.max(recipeTier, 5);
                }, ofBlock(PhotonControllerUpgrade, 4)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[5];
                    x.recipeTier = Math.max(recipeTier, 6);
                }, ofBlock(PhotonControllerUpgrade, 5)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[6];
                    x.recipeTier = Math.max(recipeTier, 7);
                }, ofBlock(PhotonControllerUpgrade, 6)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[7];
                    x.recipeTier = Math.max(recipeTier, 8);
                }, ofBlock(PhotonControllerUpgrade, 7)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[8];
                    x.recipeTier = Math.max(recipeTier, 9);
                }, ofBlock(PhotonControllerUpgrade, 8)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[9];
                    x.recipeTier = Math.max(recipeTier, 10);
                }, ofBlock(PhotonControllerUpgrade, 9)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[10];
                    x.recipeTier = Math.max(recipeTier, 11);
                }, ofBlock(PhotonControllerUpgrade, 10)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[11];
                    x.recipeTier = Math.max(recipeTier, 12);
                }, ofBlock(PhotonControllerUpgrade, 11)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[12];
                    x.enablePerfectOverclockSignal = true;
                    x.recipeTier = Math.max(recipeTier, 13);
                }, ofBlock(PhotonControllerUpgrade, 12)), onElementPass(x -> {
                    x.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[13];
                    x.enablePerfectOverclockSignal = true;
                    x.recipeTier = Math.max(recipeTier, 14);
                }, ofBlock(PhotonControllerUpgrade, 13)), ofBlock(GregTechAPI.sBlockCasings8, 7)))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivialBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            horizontalOffSet,
            verticalOffSet,
            depthOffSet,
            elementBudget,
            env,
            false,
            true);
    }

    // endregion

    // region Processing Logic

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (this.mode) {
            return GTCMRecipe.PreciseHighEnergyPhotonicQuantumMasterRecipes;
        }
        return RecipeMaps.laserEngraverRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(GTCMRecipe.PreciseHighEnergyPhotonicQuantumMasterRecipes, RecipeMaps.laserEngraverRecipes);
    }

    protected ProcessingLogic createProcessingLogic() {
        return new GTCM_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setEuModifier(getEuModifier());
                setOverclock(isEnablePerfectOverclock() ? 2 : 1, 2);
                return super.process();
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                int mRecipeTier = GTUtility.getTier(recipe.mEUt);
                if (recipeTier < 13 && recipeTier < mRecipeTier) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(mRecipeTier);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return enablePerfectOverclockSignal;
    }

    @Override
    protected float getSpeedBonus() {
        return 10000F / (10000F + totalSpeedIncrement)
            / (mode ? SpeedUpMultiplier_PhCMode_PreciseHighEnergyPhotonicQuantumMaster
                : SpeedUpMultiplier_LaserEngraverMode_PreciseHighEnergyPhotonicQuantumMaster);
    }

    @Override
    protected int getMaxParallelRecipes() {
        return this.mode ? Parallel_PhCMode_PreciseHighEnergyPhotonicQuantumMaster
            : Parallel_LaserEngraverMode_PreciseHighEnergyPhotonicQuantumMaster;
    }

    /**
     * Checks the Machine. You have to assign the MetaTileEntities for the Hatches here.
     *
     * @param aBaseMetaTileEntity
     * @param aStack
     */
    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        this.totalSpeedIncrement = 0;
        this.enablePerfectOverclockSignal = false;
        return checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (getBaseMetaTileEntity().isServerSide()) {
            this.mode = !this.mode;
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal("PreciseHighEnergyPhotonicQuantumMaster.mode." + (this.mode ? 1 : 0)));
        }
    }

    // endregion

    // region General Overrides

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        aNBT.setBoolean("mode", mode);
        aNBT.setBoolean("enablePerfectOverclockSignal", enablePerfectOverclockSignal);
        aNBT.setInteger("totalSpeedIncrement", totalSpeedIncrement);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        mode = aNBT.getBoolean("mode");
        enablePerfectOverclockSignal = aNBT.getBoolean("enablePerfectOverclockSignal");
        totalSpeedIncrement = aNBT.getInteger("totalSpeedIncrement");
    }

    @Override
    public String[] getInfoData() {
        String[] origin = super.getInfoData();
        String[] ret = new String[origin.length + 2];
        System.arraycopy(origin, 0, ret, 0, origin.length);
        ret[origin.length] = "Total Speed Increment: " + this.totalSpeedIncrement;
        ret[origin.length + 1] = "Enable" + EnumChatFormatting.GOLD
            + " Perfect Overclock"
            + EnumChatFormatting.RESET
            + ": "
            + this.enablePerfectOverclockSignal;
        return ret;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    // tooltips
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.Tooltip_PhC_MachineType)
            .addInfo(TextLocalization.Tooltip_PhC_00)
            .addInfo(TextLocalization.Tooltip_PhC_01)
            .addInfo(TextLocalization.Tooltip_PhC_02)
            .addInfo(TextLocalization.Tooltip_PhC_03)
            .addInfo(TextLocalization.Tooltip_PhC_04)
            .addInfo(TextLocalization.Tooltip_PhC_05)
            .addInfo(TextLocalization.Tooltip_PhC_06)
            .addInfo(TextLocalization.Tooltip_PhC_07)
            .addInfo(TextLocalization.Tooltip_PhC_08)
            .addInfo(TextLocalization.Tooltip_PhC_09)
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .addSeparator()
            .beginStructureBlock(15, 7, 9, false)
            .addController(TextLocalization.textFrontCenter)
            .addCasingInfoRange(TextLocalization.textCasingAdvIrPlated, 296, 347, false)
            .addCasingInfoRange(TextLocalization.textCasingTT_0, 0, 78, false)
            .addCasingInfoRange(TextLocalization.textUpgradeCasingAndLocation, 0, 25, false)
            .addInputHatch(TextLocalization.textCenterOfLRSides, 2)
            .addOutputHatch(TextLocalization.textCenterOfLRSides, 2)
            .addInputBus(TextLocalization.textCenterOfLRSides, 2)
            .addOutputBus(TextLocalization.textCenterOfLRSides, 2)
            .addEnergyHatch(TextLocalization.textHighPowerCasingUDSides, 3)
            .toolTipFinisher(TextLocalization.ModName);
        return tt;
    }

    /**
     * @param aTileEntity is just because the internal Variable "mBaseMetaTileEntity" is set after this Call.
     * @return a newly created and ready MetaTileEntity
     */
    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(183),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_PROCESSING_ARRAY_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_PROCESSING_ARRAY_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(183), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_PROCESSING_ARRAY)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_PROCESSING_ARRAY_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(183) };
    }

    // endregion
}
