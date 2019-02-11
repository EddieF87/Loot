package xyz.sleekstats.loot.sprites

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Array
import xyz.sleekstats.loot.screens.PlayScreen
import java.util.*
import kotlin.math.roundToInt

class TrainScheduler(val playScreen: PlayScreen, var probabilityOfArrivingOutOf10000: Int) {

    var trainArrived = false
    var totalScoresUpdated = false
    private val trains = Array<Train>()
    private val freeZone = 5;
    private var freeTime = 0F;


    fun createTrains() {
        val numberOfTrains: Int = (playScreen.viewport.worldWidth / Train.TRAIN_WIDTH).roundToInt() + 1
        print("createTrains $numberOfTrains ___")
        for (i in 0..numberOfTrains) {
            print("train $i   ${-i * Train.TRAIN_WIDTH}")
            trains.add(Train(playScreen, -i* Train.TRAIN_WIDTH))
        }
    }

    fun updateTrains(dt: Float) {
        trains.forEach { train ->
            train.update(dt)
        }
    }

    fun drawTrains(batch: SpriteBatch) {
        trains.forEach { train ->
            train.draw(batch)
        }
    }

    fun hasTrainArrived(dt: Float): Boolean {
        if(freeTime < freeZone) {
            freeTime += dt
            return false
        }
        val random = Random()
        val randomNumber = random.nextInt(10000)
        return probabilityOfArrivingOutOf10000 > randomNumber
    }

    fun reset() {
        trains.clear()
        freeTime = 0F
        trainArrived = false
        totalScoresUpdated = false
    }

}