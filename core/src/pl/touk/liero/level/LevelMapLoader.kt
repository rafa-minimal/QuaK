package pl.touk.liero.level

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.box2d.body
import ktx.box2d.filter
import ktx.math.vec2
import pl.touk.liero.Ctx
import pl.touk.liero.entity.entity
import pl.touk.liero.game.cat_ground
import pl.touk.liero.gdx.luminance
import pl.touk.liero.utils.get
import pl.touk.liero.utils.loadPixmapRgba8888

class LevelMapLoader(val ctx: Ctx) {

    private val valueThreshold = 0.5f

    var width = 0f
    var height = 0f

    fun loadMap(mapName: String) {
        val bgPath = "level/$mapName/bg.png"
        val texPath = "level/$mapName/tex.png"
        val mapPath = "level/$mapName/map.png"
        val bgTex = Texture(bgPath)
        val texture = Texture(texPath)
        //val texture = Texture(mapPath)
        val map = loadPixmapRgba8888(mapPath) ?: throw IllegalArgumentException("File not found: $mapPath")
        val gridSizePx = 32
        val tileSizeMeters = 1f

        val worldWidth = map.width / gridSizePx
        val worldHeight = map.height / gridSizePx
        width = worldWidth.toFloat()
        height = worldHeight.toFloat()

        ctx.engine.entity {
            body(ctx.worldEngine.baseBody)
            texture(TextureRegion(bgTex), width, height, vec2(width/2, height/2))
        }

        val color = Color()

        for(wx in 0 until worldWidth) {
            for(wy in 0 until worldHeight) {

                val tx = wx * gridSizePx
                val ty = (worldHeight - wy) * gridSizePx

                Color.rgba8888ToColor(color, map[tx, ty])

                var count = 0
                for(x in tx until (tx + gridSizePx)) {
                    for(y in ty until (ty + gridSizePx)) {
                        Color.rgba8888ToColor(color, map[x, y])
                        if (color.r < valueThreshold || color.g < valueThreshold || color.b < valueThreshold)
                            count++
                    }
                }
                val fillRatio = count.toFloat() / (gridSizePx*gridSizePx)

                if(fillRatio > 0.2f) {
                    // static
                    ctx.engine.entity {
                        body(ctx.world.body {
                            position.set(wx + tileSizeMeters / 2f, wy + tileSizeMeters / 2f)
                            box(width = tileSizeMeters, height = tileSizeMeters) {
                                density = 1f
                                filter {
                                    categoryBits = cat_ground
                                    maskBits = cat_ground
                                }
                            }
                        })
                        texture(TextureRegion(texture, tx, ty, gridSizePx, gridSizePx), 1f, 1f)
                        if (color.r > 0.5f) {
                            energy(10f)
                        }
                    }
                }

                /*var count = 0
                for(x in tx until (tx + gridSizePx)) {
                    for(y in ty until (ty + gridSizePx)) {
                        Color.rgba8888ToColor(color, map[x, y])
                        val value = color.luminance() * color.a
                        if (value < valueThreshold)
                            count++
                    }
                }
                val fillRatio = count.toFloat() / (gridSizePx*gridSizePx)

                if(fillRatio > ctx.params.) {
                    ctx.engine.entity {
                        body(ctx.world.body {
                            position.set(wx + 0.5f, wy + 0.5f + floor)
                            box(width = 1f, height = 1f) {
                                density = 1f
                                filter {
                                    categoryBits = cat_building
                                    maskBits = mask_building
                                }
                            }
                        })
                        texture(TextureRegion(texture, tx, ty, gridSizePx, gridSizePx), 1f, 1f)
                        energy(10f)
                        script(BuildingScript(ctx))
                    }
                }*/
            }
        }
    }

    fun addbackground(mapName: String) {
        // background
        /*val bgPath = "levels/$mapName/bg.png"
        ctx.engine.entity {
            body(ctx.worldEngine.baseBody)
            texture()
        }*/
    }
}