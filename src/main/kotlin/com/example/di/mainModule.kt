package com.example.di

import com.example.data.repository.*
import com.example.domain.logicflow.Game
import com.example.domain.repository.*
import com.example.domain.usecase.*
import com.example.domain.usecase.account_management.*
import com.example.domain.usecase.authentication.*
import com.example.domain.usecase.cards.*
import com.example.domain.usecase.decks.*
import com.example.domain.usecase.friendlist.*
import de.mkammerer.argon2.Argon2Factory
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {

    single { KMongo.createClient().coroutine.getDatabase("juomapeli") }

    single<AccountRepository> { AccountRepositoryImpl(get()) }

    single<AuthenticationRepository> { AuthenticationRepositoryImpl(get()) }

    single<FriendlistRepository> { FriendlistRepositoryImpl(get()) }

    single<CardsRepository> { CardsRepositoryImpl(get()) }

    single<DecksRepository> { DecksReposotiryImpl(get()) }

    single<GameRepository> { GameRepositoryImpl(get()) }

    single { Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id) }

    single {
        AccountManagementUsecases(
            VerifyUsernameLegality(),
            GenerateHashSaltPair(get()),
            VerifyHashSaltPair(get()),
            CreateNewAccount(get()),
            GetHashSaltPair(get()),
            CheckIfUsernameExists(get())
        )
    }

    single {
        AuthenticationUsecases(
            GetUsernameAndDeleteById(get()),
            DeleteByUsername(get()),
            GenerateAccessToken(),
            GenerateRefreshToken(),
            StoreRefreshIdUsernamePair(get())
        )
    }

    single {
        FriendlistUsecases(
            GenerateRelationshipId(),
            GetRelationshipStatus(get()),
            CreateNewRelationship(get()),
            AcceptFriendRequest(get()),
            GetFriendlist(get()),
            GetRelationshipStatuses(get()),
            ParseRelationshipStatuses(),
            RemoveFriend(get())
        )
    }

    single {
        CardsUsecases(
            CreateTempImage(get()),
            CreateCard(get()),
            GetIdsByAuthors(get()),
            GetUpdatesByIds(get()),
            GetCardsByIds(get()),
            VerifyAuthority(get()),
            VerifyContentLegality(),
            UpdateCard(get()),
            DeleteCard(get(), get())
        )
    }

    single { CheckLegality() }

    single {
        DeckUsecases(
            CreateDeck(get(), get(), get()),
            GetDecksByClientIds(get(), get(), get()),
            UpdateDeck(get(), get()),
            checkLegality = get(),
            DeleteDeck(get())
        )
    }

    single {
        Game()
    }


}