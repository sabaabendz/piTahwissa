<?php

namespace App\Form;

use App\Entity\Collaborator;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\EmailType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\Length;
use Symfony\Component\Validator\Constraints\NotBlank;

class CollaboratorType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $isEdit = $options['is_edit'] ?? false;

        $builder
            ->add('name', TextType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
                'constraints' => [new NotBlank(['message' => 'Name is required.']), new Length(['min' => 2, 'max' => 255])],
            ])
            ->add('email', EmailType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
                'constraints' => [new NotBlank(['message' => 'Email is required.']), new Length(['max' => 255])],
            ])
            ->add('password', PasswordType::class, [
                'mapped' => false,
                'required' => !$isEdit,
                'attr' => [
                    'class' => 'form-control',
                    'autocomplete' => 'new-password',
                ],
                'label_attr' => ['class' => 'form-label'],
                'constraints' => $isEdit ? [] : [
                    new NotBlank([
                        'message' => 'Please enter a password',
                    ]),
                    new Length([
                        'min' => 8,
                        'minMessage' => 'Your password should be at least {{ limit }} characters',
                        'max' => 4096,
                    ]),
                ],
                'help' => $isEdit ? 'Leave blank to keep current password' : 'Minimum 8 characters',
            ])
            ->add('post', TextType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
                'constraints' => [new NotBlank(['message' => 'Post is required.']), new Length(['max' => 255])],
            ])
            ->add('team', TextType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
                'constraints' => [new NotBlank(['message' => 'Team is required.']), new Length(['max' => 255])],
            ])
            ->add('enterpriseCode', TextType::class, [
                'attr' => [
                    'class' => 'form-control',
                    'placeholder' => 'Enter enterprise code',
                ],
                'label_attr' => ['class' => 'form-label'],
                'constraints' => [new NotBlank(['message' => 'Enterprise code is required.']), new Length(['max' => 20])],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Collaborator::class,
            'is_edit' => false,
        ]);
    }
}
