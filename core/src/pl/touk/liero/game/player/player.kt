package pl.touk.liero.game.player

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import ktx.box2d.body
import ktx.box2d.filter
import pl.touk.liero.Ctx
import pl.touk.liero.PlayerScript
import pl.touk.liero.ecs.Energy
import pl.touk.liero.ecs.Entity
import pl.touk.liero.ecs.energy
import pl.touk.liero.entity.entity
import pl.touk.liero.game.PlayerControl
import pl.touk.liero.game.cat_red
import pl.touk.liero.game.joint.createWeaponJoint
import pl.touk.liero.game.mask_red
import pl.touk.liero.game.weapon.*
import pl.touk.liero.script.WinnerScript
import pl.touk.liero.system.BloodScript


fun createPlayer(ctx: Ctx, x: Float, y: Float, playerControl: PlayerControl, team: String): Entity {
    val playerBody = ctx.world.body(BodyDef.BodyType.DynamicBody) {
        position.set(x, y)
        linearDamping = 0f
        fixedRotation = true
        gravityScale = ctx.params.playerGravityScale
        circle(radius = ctx.params.playerSize / 2f) {
            density = 1f
            restitution = 0.1f
            friction = 0.1f
            filter {
                categoryBits = cat_red
                maskBits = mask_red
            }
        }
    }

    val weaponBody = ctx.world.body(BodyDef.BodyType.DynamicBody) {
        position.set(x, y)
        angularDamping = ctx.params.weaponAngularDamping
        fixedRotation = true
        box(ctx.params.weaponBodyWidth, ctx.params.weaponBodyHeight) {
            density = 1f
            restitution = 0.1f
            friction = 2f
            isSensor = true
            filter {
                categoryBits = cat_red
                maskBits = mask_red
            }
        }
    }

    val bazooka = Bazooka(ctx)
    val gun = Gun(ctx)
    val minigun = MiniGun(ctx)
    val fragzooka = Fragment(ctx)
    val grenade = Grenade(ctx)
    val kaczkosznikov = Kaczkosznikov(ctx)
    val sword = Sword(ctx)
    val weapons = listOf(bazooka, gun, minigun, fragzooka, grenade, kaczkosznikov, sword)
    val movementAnimation = createMovementAnimation(ctx)
    val idleAnimation = createStandAnimation(ctx)
    val hurtAnimation = createHurtAnimation(ctx)
    val state = PlayerState(bazooka,weapons)

    val player = ctx.engine.entity {
        body(playerBody)
        joint(ctx.world.createJoint(createWeaponJoint(ctx, playerBody, weaponBody)))
        texture(ctx.gameAtlas.findRegion("circle"), ctx.params.playerSize, ctx.params.playerSize, scale = 1.6f)
        script(PlayerScript(ctx, playerControl, state, movementAnimation, idleAnimation, hurtAnimation, team))
        script(BloodScript(ctx))
        script(WinnerScript(ctx, if (team == "left") ctx.leftFrags else ctx.rightFrags))
        // can be only one render script per Entity
        renderScript(HealthAndAmmoBar(ctx, state, weaponBody))
    }

    val weapon = ctx.engine.entity {
        body(weaponBody)
        texture(bazooka.texture.copy())
    }
    sword.weapon = weapon

    ctx.actions.schedule(ctx.params.playerImmortalityTime) {
        player[energy] = Energy(ctx.params.playerTotalHealth)
    }

    return player
}

private fun createMovementAnimation(ctx: Ctx): Animation<TextureRegion> {
    val walkFrames: Array<TextureRegion> = Array()
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation0"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation1"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation2"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation3"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation4"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation5"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation6"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation7"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation8"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation9"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation10"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation11"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation12"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation13"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobAnimation14"))
    return Animation(0.025f, walkFrames, Animation.PlayMode.LOOP)
}

private fun createStandAnimation(ctx: Ctx): Animation<TextureRegion> {
    val walkFrames: Array<TextureRegion> = Array()
    walkFrames.add(ctx.gameAtlas.findRegion("blobIdle0"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobIdle1"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobIdle2"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobIdle3"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobIdle4"))
    walkFrames.add(ctx.gameAtlas.findRegion("blobIdle5"))
    return Animation(0.05f, walkFrames, Animation.PlayMode.LOOP)
}

private fun createHurtAnimation(ctx: Ctx): Animation<TextureRegion> {
    val hurtFrames: Array<TextureRegion> = Array()
    hurtFrames.add(ctx.gameAtlas.findRegion("blobHurtAnimation0"))
    hurtFrames.add(ctx.gameAtlas.findRegion("blobHurtAnimation1"))
    hurtFrames.add(ctx.gameAtlas.findRegion("blobHurtAnimation2"))
    hurtFrames.add(ctx.gameAtlas.findRegion("blobHurtAnimation3"))
    return Animation(0.05f, hurtFrames, Animation.PlayMode.LOOP)
}
